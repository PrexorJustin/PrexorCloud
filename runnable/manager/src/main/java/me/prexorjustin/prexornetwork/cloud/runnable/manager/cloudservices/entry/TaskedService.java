package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyChangeStateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyPreparedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceChangeStateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServicePreparedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceProcess;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServiceList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutCloudProxyChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutCloudServiceChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceDisconnected;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServicePrepared;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.interfaces.ITaskedService;

import java.util.Timer;
import java.util.TimerTask;

@Getter
public class TaskedService implements ITaskedService {

    private final TaskedEntry entry;
    private final String restAPIRoute;
    private ServiceProcess process;
    @Accessors(fluent = true)
    @Setter
    private boolean hasStartedNew;
    private Timer timer;

    public TaskedService(TaskedEntry entry) {
        this.entry = entry;
        this.hasStartedNew = false;
        this.timer = new Timer();
        this.restAPIRoute = WebServer.Routes.CLOUDSERVICE.getRoute().replace("%servicename%", entry.getServiceName().replace(PrexorCloudManager.config.getSplitter(), "~"));

        LiveServices liveServices = LiveServices.builder()
                .group(entry.getGroupName())
                .name(entry.getServiceName())
                .players(0)
                .host(PrexorCloudManager.config.getNodes().stream().filter(managerConfigNodes -> managerConfigNodes.getName().equals(entry.getTaskNode())).toList().getFirst().getAddress())
                .node(entry.getTaskNode())
                .port(-1)
                .uuid(entry.getUseId())
                .state(ServiceState.QUEUED)
                .lastReaction(-1)
                .build();

        LiveServiceList list = (LiveServiceList) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute()), LiveServiceList.class);
        list.getCloudServices().add(entry.getServiceName());

        Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute(), new ConfigDriver().convert(list));

        String convert = new ConfigDriver().convert(liveServices);
        Driver.getInstance().getWebServer().addRoute(new RouteEntry(this.restAPIRoute, convert));

        boolean proxy = Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getGroupType().equals("PROXY");
        if (proxy)
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyPreparedEvent(entry.getServiceName(), entry.getGroupName(), entry.getTaskNode()));
        else
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServicePreparedEvent(entry.getServiceName(), entry.getGroupName(), entry.getTaskNode()));

        NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServicePrepared(entry.getServiceName(), proxy, entry.getGroupName(), entry.getTaskNode()));
    }

    @SneakyThrows
    @Override
    public void handleExecute(String line) {
        if (this.entry.getTaskNode().equalsIgnoreCase("InternalNode")) {
            this.process.getProcess().getOutputStream().write((line + "\n").getBytes());
            this.process.getProcess().getOutputStream().flush();
        } else
            NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(
                    this.entry.getTaskNode(),
                    new PacketOutSendCommand(line, this.entry.getServiceName())
            );
    }

    @Override
    public void handleSync() {
        if (this.entry.getTaskNode().equalsIgnoreCase("InternalNode")) {
            this.process.sync();
        } else
            NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(
                    this.entry.getTaskNode(),
                    new PacketOutSyncService(this.entry.getServiceName())
            );
    }

    @Override
    public void handleLaunch() {
        if (this.entry.getTaskNode().equalsIgnoreCase("InternalNode")) {
            Driver.getInstance().getTerminalDriver().log(
                    Type.INFO,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("task-service-start")
                            .replace("%service%", this.entry.getServiceName())
                            .replace("%node%", this.entry.getTaskNode())
                            .replace("%port%", String.valueOf(this.entry.getUsedPort()))
            );

            LiveServices liveServices = (LiveServices) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(this.restAPIRoute), LiveServices.class);
            liveServices.setPort(this.entry.getUsedPort());

            String convert = new ConfigDriver().convert(liveServices);
            Driver.getInstance().getWebServer().updateRoute(this.restAPIRoute, convert);

            this.process = new ServiceProcess(
                    Driver.getInstance().getGroupDriver().load(this.entry.getGroupName()),
                    this.entry.getServiceName(),
                    this.entry.getUsedPort(),
                    this.entry.isUseProtocol()
            );
            this.process.launch();
        } else
            NettyDriver.getInstance().getNettyServer().sendPacketSynchronized(
                    this.entry.getTaskNode(),
                    new PacketOutLaunchService(
                            this.entry.getServiceName(),
                            new ConfigDriver().convert(Driver.getInstance().getGroupDriver().load(this.entry.getGroupName())),
                            this.entry.isUseProtocol()
                    )
            );
    }

    @Override
    public void handleScreen() {
        if (this.entry.getTaskNode().equalsIgnoreCase("InternalNode")) {
            if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
                this.timer.cancel();
                this.process.handleConsole();
                Driver.getInstance().getTerminalDriver().leaveSetup();
            } else {
                Driver.getInstance().getMessageStorage().setOpenServiceScreen(true);
                Driver.getInstance().getMessageStorage().setScreenForm(this.entry.getServiceName());
                Driver.getInstance().getTerminalDriver().clearScreen();
                this.process.handleConsole();
                this.timer = new Timer();
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!Driver.getInstance().getMessageStorage().getConsoleInput().isEmpty()) {
                            String line = Driver.getInstance().getMessageStorage().getConsoleInput().removeFirst();

                            if (line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("leave ")) {
                                handleScreen();
                            } else {
                                try {
                                    handleExecute(line);
                                } catch (Exception exception) {
                                    Driver.getInstance().getMessageStorage().setOpenServiceScreen(false);
                                    Driver.getInstance().getMessageStorage().setScreenForm("");
                                }
                            }
                        } else {
                            if (!Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
                                Driver.getInstance().getMessageStorage().setOpenServiceScreen(true);
                                timer.cancel();
                                process.handleConsole();
                                Driver.getInstance().getTerminalDriver().leaveSetup();
                            }
                        }
                    }
                }, 100, 100);
            }
        } else {
            if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
                this.timer.cancel();
                NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(
                        this.entry.getTaskNode(),
                        new PacketOutDisableConsole(this.entry.getServiceName())
                );
                Driver.getInstance().getTerminalDriver().leaveSetup();
            } else {
                Driver.getInstance().getMessageStorage().setOpenServiceScreen(true);
                Driver.getInstance().getMessageStorage().setScreenForm(this.entry.getServiceName());
                Driver.getInstance().getTerminalDriver().clearScreen();
                NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(
                        this.entry.getTaskNode(),
                        new PacketOutEnableConsole(this.entry.getServiceName())
                );
                this.timer = new Timer();
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String line = Driver.getInstance().getMessageStorage().getConsoleInput().removeFirst();

                        if (line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("leave ")) {
                            handleScreen();
                        } else {
                            handleExecute(line);
                        }
                    }
                }, 100, 100);
            }
        }
    }

    @Override
    public void handleQuit() {
        Driver.getInstance().getWebServer().removeRoute(this.restAPIRoute);
        NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutServiceDisconnected(this.entry.getServiceName(), Driver.getInstance().getGroupDriver().load(this.entry.getGroupName()).getGroupType().equalsIgnoreCase("PROXY")));

        LiveServiceList list = (LiveServiceList) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute()), LiveServiceList.class);
        list.remove(entry.getServiceName());
        Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute(), new ConfigDriver().convert(list));

        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen() && Driver.getInstance().getMessageStorage().getScreenForm().equals(this.entry.getServiceName())) {
            Driver.getInstance().getTerminalDriver().leaveSetup();
            this.process.handleConsole();
            this.timer.cancel();
        }

        Driver.getInstance().getTerminalDriver().log(
                Type.INFO,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("task-service-shutdown")
                        .replace("%service%", this.entry.getServiceName())
                        .replace("%uuid%", this.entry.getUuid())
        );

        if (this.entry.getTaskNode().equalsIgnoreCase("InternalNode")) {
            this.process.shutdown();
        } else {
            if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(entry.getTaskNode()))
                NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(
                        this.entry.getTaskNode(),
                        new PacketOutStopService(entry.getServiceName())
                );
        }
    }

    @Override
    public void handleRestart() {
        LiveServices liveServices = (LiveServices) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(this.restAPIRoute), LiveServices.class);
        liveServices.setState(ServiceState.STARTED);

        Driver.getInstance().getWebServer().updateRoute(this.restAPIRoute, new ConfigDriver().convert(liveServices));
        NettyDriver.getInstance().getNettyServer().removeChannel(this.entry.getServiceName());

        this.entry.setServiceState(ServiceState.STARTED);

        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
            Driver.getInstance().getTerminalDriver().leaveSetup();
            this.process.handleConsole();
            this.timer.cancel();
        }

        Driver.getInstance().getTerminalDriver().log(
                Type.INFO,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("task-service-shutdown")
                        .replace("%service%", this.entry.getServiceName())
                        .replace("%uuid%", this.entry.getUuid())
        );

        Driver.getInstance().getTerminalDriver().log(
                Type.INFO,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("task-service-start")
                        .replace("%service%", this.entry.getServiceName())
                        .replace("%node%", this.entry.getTaskNode())
                        .replace("%port%", "" + this.entry.getUsedPort())
        );

        if (this.entry.getTaskNode().equals("InternalNode"))
            process.restart();
        else
            NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(this.entry.getTaskNode(), new PacketOutRestartService(this.entry.getServiceName()));
    }

    @Override
    public void handleStatusChange(ServiceState status) {
        this.entry.setServiceState(status);
        LiveServices liveServices = (LiveServices) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(this.restAPIRoute), LiveServices.class);
        liveServices.setState(status);

        if (Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getGroupType().equals("PROXY")) {
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyChangeStateEvent(this.entry.getServiceName(), status.toString()));
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutCloudProxyChangeState(this.entry.getServiceName(), status.toString()));
        } else {
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServiceChangeStateEvent(this.entry.getServiceName(), status.toString()));
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutCloudServiceChangeState(this.entry.getServiceName(), status.toString()));
        }

        String convert = new ConfigDriver().convert(liveServices);
        Driver.getInstance().getWebServer().updateRoute(this.restAPIRoute, convert);

        if (status == ServiceState.IN_GAME) {
            this.hasStartedNew = true;
            if (this.entry.getTaskNode().equals("InternalNode")) {
                int freeMemory = Driver.getInstance().getMessageStorage().getCanUseMemory();
                int memoryAfter = freeMemory - Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getUsedMemory();

                if (memoryAfter >= 0) {
                    Integer id = PrexorCloudManager.serviceDriver.getFreeUUID(entry.getGroupName());
                    TaskedService taskedService = PrexorCloudManager.serviceDriver.register(
                            new TaskedEntry(
                                    PrexorCloudManager.serviceDriver.getFreePort(Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getGroupType().equalsIgnoreCase("PROXY")),
                                    this.entry.getGroupName(),
                                    this.entry.getGroupName() + PrexorCloudManager.config.getSplitter() + id,
                                    "InternalNode",
                                    this.entry.isUseProtocol(),
                                    String.valueOf(id),
                                    false,
                                    ""
                            )
                    );

                    Driver.getInstance().getMessageStorage().setCanUseMemory(Driver.getInstance().getMessageStorage().getCanUseMemory() - Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getUsedMemory());
                    taskedService.handleLaunch();
                }
            }
        }
    }

    @Override
    public void handleCloudPlayerConnection(boolean connect) {
        this.entry.setCurrentPlayers(this.entry.getCurrentPlayers() + (connect ? +1 : -1));
        LiveServices liveServices = (LiveServices) new ConfigDriver().convert(PrexorCloudManager.restDriver.get(this.restAPIRoute), LiveServices.class);
        liveServices.setPlayers(entry.getCurrentPlayers());
        Driver.getInstance().getWebServer().updateRoute(this.restAPIRoute, new ConfigDriver().convert(liveServices));
    }
}
