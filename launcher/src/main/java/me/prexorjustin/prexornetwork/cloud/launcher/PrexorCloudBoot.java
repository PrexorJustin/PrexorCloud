package me.prexorjustin.prexornetwork.cloud.launcher;

import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig.NodeConfig;
import me.prexorjustin.prexornetwork.cloud.driver.event.EventDriver;
import me.prexorjustin.prexornetwork.cloud.driver.language.LanguageDriver;
import me.prexorjustin.prexornetwork.cloud.driver.language.entry.LanguageConfig;
import me.prexorjustin.prexornetwork.cloud.driver.storage.ModuleStorage;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.TerminalDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.launcher.loader.InstanceLoader;
import me.prexorjustin.prexornetwork.cloud.launcher.loader.InstanceLoaderTest;
import me.prexorjustin.prexornetwork.cloud.launcher.update.AutoUpdater;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class PrexorCloudBoot {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @SneakyThrows
    public static void main(String[] args) {
        new Driver();

        if (!Files.exists(Paths.get("./service.json")) && !Files.exists(Paths.get("./nodeservice.json")))
            Driver.getInstance().getMessageStorage().setLanguage("ENGLISH");
        else {
            if (Files.exists(Paths.get("./nodeservice.json"))) {
                NodeConfig config = (NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class);

                Driver.getInstance().getMessageStorage().setLanguage(config.getLanguage());
            } else if (Files.exists(Paths.get("./service.json"))) {
                ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

                Driver.getInstance().getMessageStorage().setLanguage(config.getLanguage());
            }
        }

        Driver.getInstance().setLanguageDriver(new LanguageDriver());
        Driver.getInstance().getMessageStorage().setEventDriver(new EventDriver());

        Driver.getInstance().setTerminalDriver(new TerminalDriver());
        Driver.getInstance().getTerminalDriver().clearScreen();
        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());

        String version = System.getProperty("java.version");
        int majorVersion = Integer.parseInt(version.split("\\.")[0]);

        if (majorVersion < 17) {
            System.out.println("PrexorCloud is running on Java version " + version);
            System.out.println("Please use Java 17 or higher!");
            System.exit(0);
            return;
        }

        if (System.getProperty("user.name").equalsIgnoreCase("ROOT")) {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("no-root-running"));
        }

        Files.deleteIfExists(Paths.get("./OLD.jar"));

        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-check-if-setup-has-finished"));

        if (!Files.exists(Paths.get("./service.json")) && !Files.exists(Paths.get("./nodeservice.json"))) {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-first-starting-cloud"));
            Thread.sleep(2000);
            waitForFinishedSetup();
            Driver.getInstance().getTerminalDriver().joinSetup();
        } else {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-has-already-finished"));
            runClient();
        }
    }

    @SneakyThrows
    private static void runClient() {
        boolean autoUpdate = false;

        if (Files.exists(Paths.get("./service.json"))) {
            String[] paths = {
                    "./local/GLOBAL/EVERY/plugins/",
                    "./local/GLOBAL/EVERY_SERVER/plugins/",
                    "./local/GLOBAL/EVERY_PROXY/plugins/",
                    "./local/GLOBAL/EVERY_LOBBY/plugins/",
                    "./local/storage/",
                    "./local/groups/",
                    "./local/templates/"
            };

            if (!Files.exists(Paths.get("./local/storage/messages.storage"))) {
                new ConfigDriver("./local/storage/messages.storage").save(new LanguageConfig(Driver.getInstance().getLanguageDriver().getLanguage().getData()));
            }

            Arrays.stream(paths).forEach(path -> {
                File directory = new File(path);
                if (!directory.exists()) directory.mkdirs();
            });

            autoUpdate = ((ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class)).isAutoUpdate();

        } else {
            String[] paths = {
                    "./local/GLOBAL/EVERY/plugins/",
                    "./local/GLOBAL/EVERY_SERVER/plugins/",
                    "./local/GLOBAL/EVERY_PROXY/plugins/",
                    "./local/GLOBAL/EVERY_LOBBY/plugins/",
                    "./local/templates/"
            };

            Arrays.stream(paths).forEach(path -> {
                File directory = new File(path);
                if (!directory.exists()) directory.mkdirs();
            });

            autoUpdate = ((NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class)).isAutoUpdate();
        }

        if (!Files.exists(Paths.get("./dependency/"))) {
            Path folder = Paths.get("./dependency/");
            Files.createDirectory(folder);
            Files.setAttribute(folder, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        }

        if (!Files.exists(Paths.get("./dependency/runnable-manager.jar")) && !Files.exists(Paths.get("./dependency/runnable-node.jar"))) {
            if (Files.exists(Paths.get("./service.json"))) {
                String downloadURL = Driver.getInstance().getMessageStorage().loadGeneralConfig().getConfig().get("cloud-manager");

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadURL)).GET().build();
                HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

                try (InputStream body = response.body()) {
                    Files.createDirectories(Paths.get("./dependency/runnable-manager.jar").getParent());
                    Files.write(Paths.get("./dependency/runnable-manager.jar"), body.readAllBytes());
                }
            } else {
                String downloadURL = Driver.getInstance().getMessageStorage().loadGeneralConfig().getConfig().get("cloud-node");

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadURL)).GET().build();
                HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

                try (InputStream body = response.body()) {
                    Files.write(Paths.get("./dependency/runnable-node.jar"), body.readAllBytes());
                }
            }
        }

        if (autoUpdate) {
            new AutoUpdater();
            try (Stream<Path> stream = Files.list(Paths.get("./modules/"))) {
                stream.forEach(path -> path.toFile().delete());
            }

            Paths.get("./modules/").toFile().mkdirs();
            new ModuleStorage().downloadAllModules();
        }

        if (!Files.exists(Paths.get("./modules/"))) {
            Paths.get("./modules/").toFile().mkdirs();
            new ModuleStorage().downloadAllModules();
        }

        if (Files.exists(Paths.get("./service.json")))
            new InstanceLoader(new File("./dependency/runnable-manager.jar"));
        else
            InstanceLoaderTest.loadInstance(new File("./dependency/runnable-node.jar"));
    }

    private static void waitForFinishedSetup() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!Driver.getInstance().getTerminalDriver().isInSetup()) {
                    runClient();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }
}
