package me.prexorjustin.prexornetwork.cloud.driver.process;

import lombok.Getter;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig.NodeConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.process.interfaces.IServiceProcess;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.PacketInSendConsole;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;
import java.util.stream.Stream;

@Getter
public final class ServiceProcess implements IServiceProcess {

    private final Group group;
    private final String service;
    private final int port;
    private final boolean useProtocol;
    private final LinkedList<String> consoleStorage;
    private Process process;
    private boolean useCustomTemplate;
    private String customTemplate;
    private boolean useVelocity, useConsole;
    private BufferedReader reader;

    public ServiceProcess(Group group, String service, int port, boolean useProtocol) {
        this.group = group;
        this.service = service;
        this.port = port;
        this.useProtocol = useProtocol;
        this.useVelocity = false;
        this.consoleStorage = new LinkedList<>();
        this.useConsole = false;
        this.useCustomTemplate = false;
        this.customTemplate = "";
    }

    @SneakyThrows
    @Override
    public void sync() {
        if (this.process == null || this.port == 0 || this.service == null || this.group == null) return;

        new TimerBase().scheduleAsync(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                Path liveServiceLocation = Paths.get("./live/" + group.getName() + "/" + service + "/");
                Path templateLocation = Paths.get("./local/templates/" + group.getStorage().getTemplate() + "/");

                if (useCustomTemplate) {
                    File customTemplateFile = new File(customTemplate);
                    customTemplateFile.deleteOnExit();
                    customTemplateFile.mkdirs();

                    FileUtils.copyDirectory(liveServiceLocation.toFile(), customTemplateFile);
                } else if (!group.isRunStatic()) {
                    Files.deleteIfExists(templateLocation);
                    templateLocation.toFile().mkdirs();

                    FileUtils.copyDirectory(liveServiceLocation.toFile(), templateLocation.toFile());
                } else {
                    Files.deleteIfExists(templateLocation);
                    templateLocation.toFile().mkdirs();

                    FileUtils.copyDirectory(liveServiceLocation.toFile(), Paths.get(templateLocation.toString(), service + "/").toFile());
                    FileUtils.copyDirectory(Paths.get(liveServiceLocation.toString(), "plugins/").toFile(), Paths.get(templateLocation.toString(), service + "/plugins/").toFile());
                }
            }
        }, 5, TimeUtil.MILLISECONDS);
    }

    @Override
    public void handleConsole() {
        if (this.useConsole) this.useConsole = false;
        else {
            this.useConsole = true;

            new Thread(() -> {
                String line;

                Path serviceJsonLocation = Paths.get("./service.json");

                this.consoleStorage.forEach(s -> {
                    if (Files.exists(serviceJsonLocation))
                        Driver.getInstance().getTerminalDriver().log(this.service, s);
                    else
                        NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(new PacketInSendConsole(this.service, s));
                });
                try {
                    while ((line = this.reader.readLine()) != null && this.useConsole) {
                        this.consoleStorage.add(line);
                        if (Files.exists(serviceJsonLocation))
                            Driver.getInstance().getTerminalDriver().log(this.service, line);
                        else
                            NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(new PacketInSendConsole(this.service, line));
                    }
                } catch (Exception exception) {
                    Driver.getInstance().getMessageStorage().setOpenServiceScreen(false);
                }
            }).start();
        }
    }


    @SneakyThrows
    @Override
    public void launch() {
        if (this.process == null || this.port == 0 || this.service == null || this.group == null) return;

        if (!Driver.getInstance().getTemplateDriver().get().contains(this.group.getStorage().getTemplate()))
            Driver.getInstance().getTemplateDriver().create(
                    this.group.getStorage().getTemplate(),
                    this.group.getGroupType().equalsIgnoreCase("PROXY"),
                    this.group.isRunStatic()
            );

        Paths.get("./live/" + group.getName() + "/" + service + "/plugins/").toFile().mkdirs();

        Path groupTemplateLocation = Paths.get("./local/templates/" + group.getName() + "/");
        Path defaultTemplateLocation = Paths.get("./local/templates/" + group.getName() + "/default/");

        if (!this.useCustomTemplate) {
            if (this.group.isRunStatic()) {
                if (!Files.exists(defaultTemplateLocation)) {
                    defaultTemplateLocation.toFile().mkdirs();

                    try (Stream<Path> stream = Files.list(groupTemplateLocation)) {
                        stream.forEach(path -> {
                            try {
                                if (!path.getFileName().toString().equalsIgnoreCase("default")) {
                                    Files.move(path, defaultTemplateLocation.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (IOException ignored) {
                            }
                        });
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            } else {
                if (Files.exists(defaultTemplateLocation)) {
                    try (Stream<Path> stream = Files.list(groupTemplateLocation)) {
                        stream.forEach(path -> {
                            if (!path.toFile().getName().equalsIgnoreCase("default") && Files.isDirectory(path)) {
                                try {
                                    FileUtils.deleteDirectory(path.toFile());
                                } catch (IOException ignored) {
                                }
                            }
                        });

                        this.moveFiles(defaultTemplateLocation, groupTemplateLocation);
                        defaultTemplateLocation.toFile().delete();
                    }
                }
            }
        }

        Path serviceLiveDirectoryLocation = Paths.get("./live/" + group.getName() + "/" + service + "/");

        if (this.useCustomTemplate)
            Driver.getInstance().getTemplateDriver().copy(this.customTemplate, serviceLiveDirectoryLocation.toString());
        else if (!this.group.isRunStatic())
            Driver.getInstance().getTemplateDriver().copy(group.getStorage().getTemplate(), serviceLiveDirectoryLocation.toString());
        else {
            if (Files.exists(groupTemplateLocation))
                FileUtils.copyDirectory(groupTemplateLocation.toFile(), serviceLiveDirectoryLocation.toFile());
            else
                FileUtils.copyDirectory(defaultTemplateLocation.toFile(), serviceLiveDirectoryLocation.toFile());
        }

        if (!Driver.getInstance().getModuleDriver().getLoadedModules().isEmpty()) {
            Driver.getInstance().getModuleDriver().getLoadedModules().forEach(moduleLoader -> {
                Path moduleLocation = Paths.get("./modules/" + moduleLoader.getJarName() + ".jar");
                Path moduleLiveLocation = Paths.get("./live/" + group.getName() + "/" + service + "/plugins/" + moduleLoader.getJarName() + ".jar");

                try {
                    switch (moduleLoader.getConfiguration().copy().toUpperCase()) {
                        case "ALL":
                            FileUtils.copyFile(moduleLocation.toFile(), moduleLiveLocation.toFile());
                            break;
                        case "LOBBY":
                            if (this.group.getGroupType().equalsIgnoreCase("LOBBY")) {
                                FileUtils.copyFile(moduleLocation.toFile(), moduleLiveLocation.toFile());
                            }
                        case "PROXY":
                            if (this.group.getGroupType().equalsIgnoreCase("PROXY")) {
                                FileUtils.copyFile(moduleLocation.toFile(), moduleLiveLocation.toFile());
                            }
                        case "SERVER":
                            if (!this.group.getGroupType().equalsIgnoreCase("PROXY")) {
                                FileUtils.copyFile(moduleLocation.toFile(), moduleLiveLocation.toFile());
                            }
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Path servicePath = Paths.get("./service.json");
        Path nodeServicePath = Paths.get("./nodeservice.json");
        LiveService liveService = new LiveService();

        liveService.setService(this.service);
        liveService.setGroup(this.group.getName());
        liveService.setPort(this.port);

        if (Files.exists(servicePath)) {
            ManagerConfig config = (ManagerConfig) new ConfigDriver(servicePath.toString()).read(ManagerConfig.class);

            liveService.setManagerAddress(config.getManagerAddress());
            liveService.setRunningNode("InternalNode");
            liveService.setRestPort(config.getRestPort());
            liveService.setNetworkingPort(config.getNetworkingPort());
            this.useVelocity = config.getBungeeVersion().equalsIgnoreCase("VELOCITY");
        } else {
            NodeConfig config = (NodeConfig) new ConfigDriver(nodeServicePath.toString()).read(NodeConfig.class);

            liveService.setManagerAddress(config.getManagerAddress());
            liveService.setRunningNode(config.getNodeName());
            liveService.setRestPort(config.getRestPort());
            liveService.setNetworkingPort(config.getNetworkingPort());
            this.useVelocity = config.getBungeeVersion().equalsIgnoreCase("VELOCITY");
        }

        new ConfigDriver("./live/" + this.group.getName() + "/" + this.service + "/CLOUDSERVICE.json").save(liveService);

        FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY"), serviceLiveDirectoryLocation.toFile());
        FileUtils.copyFile(new File("./connection.key"), serviceLiveDirectoryLocation.resolve("/connection.key").toFile());

        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.directory(serviceLiveDirectoryLocation.toFile());
        if (!this.group.getStorage().getJavaEnvironment().isEmpty())
            processBuilder.environment().put("JAVA_HOME", this.group.getStorage().getJavaEnvironment());

        if (this.group.getGroupType().equalsIgnoreCase("PROXY")) {
            FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_PROXY"), serviceLiveDirectoryLocation.toFile());

            String[] command = new String[]{
                    "java",
                    "-XX:+UseG1GC",
                    "-XX:G1HeapRegionSize=4M",
                    "-XX:+UnlockExperimentalVMOptions",
                    "XX:+ParallelRefProcEnabled",
                    "-XX:+AlwaysPreTouch",
                    "-XX:MaxInlineLevel=15",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:-UseAdaptiveSizePolicy",
                    "-XX:CompileThreshold=100",
                    "-Dio.netty.recycler.maxCapacity=0",
                    "-Dio.netty.recycler.maxCapacity.default=0",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Xmx" + this.group.getUsedMemory() + "M",
                    "-jar",
                    "server.jar",
                    this.group.getStorage().getStartArguments()
            };

            if (this.useVelocity) {
                Files.writeString(
                        serviceLiveDirectoryLocation.resolve("velocity.toml"),
                        Driver.getInstance().getMessageStorage().getVelocityToml(this.port, this.group.getMaxPlayer(), this.useProtocol),
                        StandardOpenOption.CREATE
                );

                String generatedString = new Random().ints(97, 122 + 1)
                        .limit(10)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                Files.writeString(serviceLiveDirectoryLocation.resolve("forwarding.secret"), generatedString, StandardOpenOption.CREATE);
            } else {
                Files.writeString(
                        serviceLiveDirectoryLocation.resolve("config.yml"),
                        Driver.getInstance().getMessageStorage().getBungeecordConfig(this.port, this.group.getMaxPlayer(), this.useProtocol),
                        StandardOpenOption.CREATE
                );
            }

            processBuilder.command(command);
        } else {
            FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_SERVER"), serviceLiveDirectoryLocation.toFile());

            if (this.group.getGroupType().equalsIgnoreCase("LOBBY"))
                FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_LOBBY"), serviceLiveDirectoryLocation.toFile());

            String[] command = new String[]{
                    "java",
                    "-Xms" + this.group.getUsedMemory() + "G",
                    "-Xmx" + this.group.getUsedMemory() + "G",
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                    "-XX:MaxGCPauseMillis=200",
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:+DisableExplicitGC",
                    "-XX:+AlwaysPreTouch",
                    "-XX:G1NewSizePercent=30",
                    "XX:G1MaxNewSizePercent=40",
                    "-XX:G1HeapRegionSize=8M",
                    "-XX:G1ReservePercent=20",
                    "-XX:G1HeapWastePercent=5",
                    "-XX:G1MixedGCCountTarget=4",
                    "-XX:InitiatingHeapOccupancyPercent=15",
                    "-XX:G1MixedGCLiveThresholdPercent=90",
                    "Dio.netty.recycler.maxCapacity=0",
                    "Dio.netty.recycler.maxCapacity.default=0",
                    "XX:-UseAdaptiveSizePolicy",
                    "Xx:+PerfDisableSharedMem",
                    "-XX:MaxTenuringThreshold=1",
                    "-Dcom.mojang.eula.agree=true",
                    "-Dio.netty.recycler.maxCapacity=0",
                    "-Dio.netty.recycler.maxCapacity.default=0",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "Dusing.aikars.flags=https://mcflags.emc.gs",
                    "Daikars.new.flags=true",
                    "-jar",
                    "server.jar",
                    "nogui",
                    "--nojline",
                    this.group.getStorage().getStartArguments()
            };

            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("server.properties"),
                    Driver.getInstance().getMessageStorage().getSpigotServerProperties(this.port),
                    StandardOpenOption.CREATE
            );
            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("bukkit.yml"),
                    Driver.getInstance().getMessageStorage().getBukkitYML(),
                    StandardOpenOption.CREATE
            );
            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("spigot.yml"),
                    Driver.getInstance().getMessageStorage().getSpigotYML(),
                    StandardOpenOption.CREATE
            );

            processBuilder.command(command);
        }

        this.process = processBuilder.start();
        this.reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
    }

    @SneakyThrows
    @Override
    public void restart() {
        if (process != null && process.isAlive()) {
            process.destroy();
            process.destroyForcibly().destroy();
        }

        Path serviceLiveDirectoryLocation = Paths.get("./live/" + group.getName() + "/" + service + "/");
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.directory(serviceLiveDirectoryLocation.toFile());
        if (!this.group.getStorage().getJavaEnvironment().isEmpty())
            processBuilder.environment().put("JAVA_HOME", this.group.getStorage().getJavaEnvironment());

        if (this.group.getGroupType().equalsIgnoreCase("PROXY")) {
            FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_PROXY"), serviceLiveDirectoryLocation.toFile());

            String[] command = new String[]{
                    "java",
                    "-XX:+UseG1GC",
                    "-XX:G1HeapRegionSize=4M",
                    "-XX:+UnlockExperimentalVMOptions",
                    "XX:+ParallelRefProcEnabled",
                    "-XX:+AlwaysPreTouch",
                    "-XX:MaxInlineLevel=15",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:-UseAdaptiveSizePolicy",
                    "-XX:CompileThreshold=100",
                    "-Dio.netty.recycler.maxCapacity=0",
                    "-Dio.netty.recycler.maxCapacity.default=0",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Xmx" + this.group.getUsedMemory() + "M",
                    "-jar",
                    "server.jar",
                    this.group.getStorage().getStartArguments()
            };

            if (this.useVelocity) {
                Files.writeString(
                        serviceLiveDirectoryLocation.resolve("velocity.toml"),
                        Driver.getInstance().getMessageStorage().getVelocityToml(this.port, this.group.getMaxPlayer(), this.useProtocol),
                        StandardOpenOption.CREATE
                );

                String generatedString = new Random().ints(97, 122 + 1)
                        .limit(10)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
                Files.writeString(serviceLiveDirectoryLocation.resolve("forwarding.secret"), generatedString, StandardOpenOption.CREATE);
            } else {
                Files.writeString(
                        serviceLiveDirectoryLocation.resolve("config.yml"),
                        Driver.getInstance().getMessageStorage().getBungeecordConfig(this.port, this.group.getMaxPlayer(), this.useProtocol),
                        StandardOpenOption.CREATE
                );
            }

            processBuilder.command(command);
        } else {
            FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_SERVER"), serviceLiveDirectoryLocation.toFile());

            if (this.group.getGroupType().equalsIgnoreCase("LOBBY"))
                FileUtils.copyDirectory(new File("./local/GLOBAL/EVERY_LOBBY"), serviceLiveDirectoryLocation.toFile());

            String[] command = new String[]{
                    "java",
                    "-Xms" + this.group.getUsedMemory() + "G",
                    "-Xmx" + this.group.getUsedMemory() + "G",
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                    "-XX:MaxGCPauseMillis=200",
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:+DisableExplicitGC",
                    "-XX:+AlwaysPreTouch",
                    "-XX:G1NewSizePercent=30",
                    "XX:G1MaxNewSizePercent=40",
                    "-XX:G1HeapRegionSize=8M",
                    "-XX:G1ReservePercent=20",
                    "-XX:G1HeapWastePercent=5",
                    "-XX:G1MixedGCCountTarget=4",
                    "-XX:InitiatingHeapOccupancyPercent=15",
                    "-XX:G1MixedGCLiveThresholdPercent=90",
                    "Dio.netty.recycler.maxCapacity=0",
                    "Dio.netty.recycler.maxCapacity.default=0",
                    "XX:-UseAdaptiveSizePolicy",
                    "Xx:+PerfDisableSharedMem",
                    "-XX:MaxTenuringThreshold=1",
                    "-Dcom.mojang.eula.agree=true",
                    "-Dio.netty.recycler.maxCapacity=0",
                    "-Dio.netty.recycler.maxCapacity.default=0",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "Dusing.aikars.flags=https://mcflags.emc.gs",
                    "Daikars.new.flags=true",
                    "-jar",
                    "server.jar",
                    "nogui",
                    "--nojline",
                    this.group.getStorage().getStartArguments()
            };

            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("server.properties"),
                    Driver.getInstance().getMessageStorage().getSpigotServerProperties(this.port),
                    StandardOpenOption.CREATE
            );
            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("bukkit.yml"),
                    Driver.getInstance().getMessageStorage().getBukkitYML(),
                    StandardOpenOption.CREATE
            );
            Files.writeString(
                    serviceLiveDirectoryLocation.resolve("spigot.yml"),
                    Driver.getInstance().getMessageStorage().getSpigotYML(),
                    StandardOpenOption.CREATE
            );

            processBuilder.command(command);
        }

        this.process = processBuilder.start();
        this.reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
    }

    @SneakyThrows
    @Override
    public void shutdown() {
        if (process != null && process.isAlive()) {
            process.destroy();
            process.destroyForcibly().destroy();
        }
        Thread.sleep(500);

        Path serviceLogs = Paths.get("./local/logs/services/" + group.getName() + "/" + service + ".json");
        Path liveLogs = Paths.get("/live/" + group.getName() + "/" + service + "/logs/");

        if (Files.exists(Paths.get("./service.json"))) {
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
            if (!config.isCopyLogs()) return;
        } else {
            NodeConfig config = (NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class);
            if (!config.isCopyLogs()) return;
        }

        Files.deleteIfExists(serviceLogs);
        FileUtils.copyFile(liveLogs.resolve("latest.log").toFile(), serviceLogs.toFile());

        Path liveGroupLocation = Paths.get("./live/" + group.getName() + "/");
        Path liveServiceLocation = liveGroupLocation.resolve(this.service + "/");


        if (!this.group.isRunStatic()) {
            FileUtils.deleteDirectory(liveServiceLocation.toFile());
            liveServiceLocation.toFile().deleteOnExit();
            if (FileUtils.isEmptyDirectory(liveGroupLocation.toFile()))
                Files.delete(liveGroupLocation);
        } else {
            Path localServiceTemplate = Paths.get("./local/templates/" + group.getName() + "/" + service + "/");

            Files.delete(localServiceTemplate);
            localServiceTemplate.toFile().mkdir();

            FileUtils.copyDirectory(liveServiceLocation.toFile(), localServiceTemplate.toFile());
            Thread.sleep(500);
            FileUtils.deleteDirectory(liveServiceLocation.toFile());
            Thread.sleep(200);

            if (FileUtils.isEmptyDirectory(liveGroupLocation.toFile()))
                Files.delete(liveGroupLocation);
        }

        Path liveLocation = Paths.get("./live/");
        if (FileUtils.isEmptyDirectory(liveLocation.toFile()))
            Files.delete(liveLocation);
    }

    @SneakyThrows
    private void moveFiles(Path sourcePath, Path targetPath) {
        if (Files.exists(sourcePath)) {
            try (Stream<Path> stream = Files.list(sourcePath)) {
                stream.forEach(path -> {
                    try {
                        Files.move(path, targetPath.resolve(path.getFileName()));
                    } catch (IOException ignored) {
                    }
                });
            }
        }
    }
}
