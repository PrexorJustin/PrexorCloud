package me.prexorjustin.prexornetwork.cloud.runnable.manager;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.authentication.AuthenticatorKey;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.storage.IRunAble;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.animation.AnimationDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.addresses.Addresses;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group.GroupList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServiceList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist.Whitelist;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMaintenance;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMaxPlayers;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMinCount;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandWhitelist;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.PacketOutDisableNodeConsole;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.PacketOutEnableNodeConsole;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.PacketOutSendCommandToNodeConsole;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.PacketOutShutdownNode;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerDispatchCommand;
import me.prexorjustin.prexornetwork.cloud.networking.server.NettyServer;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.CloudServiceDriver;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedService;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.queue.QueueDriver;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.commands.*;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.command.HandlePacketInCommand;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.node.PacketInNodeHandler;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.PacketInAPIHandler;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.PacketInCommandHandler;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.PacketInGroupHandler;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.PacketInServiceHandler;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.playerbased.PacketInPlayerHandler;

import java.io.File;
import java.util.*;

public class PrexorCloudManager implements IRunAble {

    public static RestDriver restDriver;
    public static QueueDriver queueDriver;
    public static CloudServiceDriver serviceDriver;

    public static ManagerConfig config;
    public static boolean shutdown;
    private static Timer timer;

    public static void shutdownHook() {
        NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutShutdownNode());
        Driver.getInstance().getModuleDriver().unload();
        Driver.getInstance().getMessageStorage().getEventDriver().clearListeners();
        NettyDriver.getInstance().getNettyServer().close();
        serviceDriver.getServicesFromNode("InternalNode").forEach(TaskedService::handleQuit);
        shutdown = true;

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        Driver.getInstance().getWebServer().close();
        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("cloud-shutting-down"));
        System.exit(0);
    }

    public static void screenNode(String node) {
        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
            timer.cancel();
            NettyDriver.getInstance().getNettyServer().sendPacketSynchronized(node, new PacketOutDisableNodeConsole());
            Driver.getInstance().getTerminalDriver().leaveSetup();
        } else {
            Driver.getInstance().getMessageStorage().setOpenServiceScreen(true);
            Driver.getInstance().getMessageStorage().setScreenForm(node);
            NettyDriver.getInstance().getNettyServer().sendPacketSynchronized(node, new PacketOutEnableNodeConsole());
            Driver.getInstance().getTerminalDriver().clearScreen();

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!Driver.getInstance().getMessageStorage().getConsoleInput().isEmpty()) {
                        String line = Driver.getInstance().getMessageStorage().getConsoleInput().removeFirst();
                        if (line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("leave ")) {
                            screenNode(Driver.getInstance().getMessageStorage().getScreenForm());
                        } else {
                            NettyDriver.getInstance().getNettyServer().sendPacketSynchronized(node, new PacketOutSendCommandToNodeConsole(line));
                        }
                    }
                }
            }, 100, 100);
        }
    }

    @Override
    public void run() {
        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("manager-try-start"));

        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("client.encoding.override", "UTF-8");
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLED");
        System.setProperty("io.netty.recycler.maxCapacity", "0");
        System.setProperty("io.netty.recycler.maxCapacity.default", "0");

        config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

        if (!new File("./connection.key").exists()) {
            AuthenticatorKey key = new AuthenticatorKey();
            String k = UUID.randomUUID() + UUID.randomUUID().toString() + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID() + UUID.randomUUID();
            key.setKey(Driver.getInstance().getMessageStorage().utf8ToBase64(k));
            new ConfigDriver("./connection.key").save(key);
        }

        Driver.getInstance().getMessageStorage().setCanUseMemory(config.getCanUseMemory() - 250);
        initNetty(config);

        restDriver = new RestDriver(config.getManagerAddress(), config.getRestPort());
        initRestService();

        if (!new File("./local/messages.json").exists()) {
            HashMap<String, String> messages = new HashMap<>();
            messages.put("prefix", "§8▷ §bPrexorCloud §8▌ §7");
            messages.put("successfullyConnected", "%PREFIX%Successfully connected to §a%service_name%");
            messages.put("serviceIsFull", "%PREFIX%§cThe service is unfortunately full");
            messages.put("alreadyOnFallback", "%PREFIX%§cYou are already on a Fallback");
            messages.put("connectingGroupMaintenance", "%PREFIX%§cThe group is in maintenance");
            messages.put("noFallbackServer", "%PREFIX%§cCould not find a suitable fallback to connect you to!");
            messages.put("kickNetworkIsFull", "§8▷ §cThe network is full buy the premium to be able to despite that on it");
            messages.put("kickNetworkIsMaintenance", "§8▷ §cthe network is currently undergoing maintenance");
            messages.put("kickNoFallback", "§8▷ §cThe server you were on went down, but no fallback server was found!");
            messages.put("kickOnlyProxyJoin", "§8▷ §cplease connect over the main proxy");
            messages.put("kickAlreadyOnNetwork", "§8▷ §cYou are already on the Network");
            messages.put("noPermsToJoinTheService", "§8▷ §cno perms to join the service");
            new ConfigDriver("./local/messages.json").save(new Messages(messages));
        }

        if (!new File("./local/GLOBAL/EVERY/plugins/prexorcloud-api.jar").exists()) {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("try-to-download-cloudapi"));
            Driver.getInstance().getMessageStorage().getPacketLoader().loadCloudAPI();
            new AnimationDriver().play();
        }

        if (!new File("./local/GLOBAL/EVERY/plugins/prexorcloud-plugin.jar").exists()) {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("try-to-download-plugin"));
            Driver.getInstance().getMessageStorage().getPacketLoader().loadCloudPlugin();
        }

        Driver.getInstance().getModuleDriver().load();

        initCommands();

        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("cloud-start-successful"));
        queueDriver = new QueueDriver();

        Messages msg = (Messages) new ConfigDriver("./local/messages.json").read(Messages.class);
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/message/default", new ConfigDriver().convert(msg)));

        GroupList groupList = new GroupList();
        groupList.setGroups(Driver.getInstance().getGroupDriver().getAllStrings());

        Driver.getInstance().getWebServer().addRoute(new RouteEntry(WebServer.Routes.GROUP_GENERAL.getRoute(), new ConfigDriver().convert(groupList)));
        Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
            if (Driver.getInstance().getWebServer().getRoute("/cloudgroup/" + group.getName()) == null)
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudgroup/" + group.getName(), new ConfigDriver().convert(group)));
            else
                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + group.getName(), new ConfigDriver().convert(group));
        });

        Driver.getInstance().initOfflinePlayerCacheDriver();

        LiveServiceList liveGroup = new LiveServiceList();
        liveGroup.setCloudServiceSplitter(config.getSplitter());
        liveGroup.setCloudServices(new ArrayDeque<>());
        Driver.getInstance().getWebServer().addRoute(new RouteEntry(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute(), new ConfigDriver().convert(liveGroup)));

        Whitelist whitelistConfig = new Whitelist();
        whitelistConfig.setWhitelist(config.getWhitelist());
        Driver.getInstance().getWebServer().addRoute(new RouteEntry(WebServer.Routes.WHITELIST.getRoute(), new ConfigDriver().convert(whitelistConfig)));

        PlayerGeneral general = new PlayerGeneral();
        general.setPlayers(new ArrayList<>());
        Driver.getInstance().getWebServer().addRoute(new RouteEntry(WebServer.Routes.PLAYER_GENERAL.getRoute(), new ConfigDriver().convert(general)));

        Addresses AddressesConfig = new Addresses();
        ArrayList<String> addresses = new ArrayList<>();
        config.getNodes().forEach(managerConfigNodes -> addresses.add(managerConfigNodes.getAddress()));
        AddressesConfig.setAddresses(addresses);
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/default/addresses", new ConfigDriver().convert(AddressesConfig)));

        serviceDriver = new CloudServiceDriver();
    }

    private void initRestService() {
        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("web-server-prepared"));
        Driver.getInstance().runWebServer();
        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("web-server-connected").replace("%port%", String.valueOf(config.getRestPort())));
    }

    public void initNetty(ManagerConfig config) {
        new NettyDriver();
        Driver.getInstance().getTerminalDriver().log(Type.NETWORK, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("netty-server-prepared"));

        NettyDriver.getInstance().getAllowedAddresses().add("127.0.0.1");
        if (!config.getNodes().isEmpty())
            config.getNodes().forEach(managerConfigNodes -> {
                if (!NettyDriver.getInstance().getAllowedAddresses().contains(managerConfigNodes.getAddress()))
                    NettyDriver.getInstance().getAllowedAddresses().add(managerConfigNodes.getAddress());
            });

        /*
         * this starts a new NettyServer with Epoll on EpollEventLoopGroup or NioEventLoopGroup basis.
         * */

        NettyDriver.getInstance().setNettyServer(new NettyServer());
        NettyDriver.getInstance().getNettyServer().bind(config.getNetworkingPort()).start();
        Driver.getInstance().getTerminalDriver().log(
                Type.NETWORK,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("netty-server-connected")
                        .replace("%address%", config.getManagerAddress())
                        .replace("%port%", "" + config.getNetworkingPort())
        );

        //PACKETS
        NettyDriver.getInstance().getPacketDriver()
                //API
                .registerHandler(new PacketInAPIPlayerMessage().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerMessage.class)
                .registerHandler(new PacketInAPIPlayerConnect().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerConnect.class)
                .registerHandler(new PacketInAPIPlayerKick().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerKick.class)
                .registerHandler(new PacketInAPIPlayerTitle().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerTitle.class)
                .registerHandler(new PacketInAPIPlayerActionBar().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerActionBar.class)
                .registerHandler(new PacketInAPIPlayerTab().getPacketUUID(), new PacketInAPIHandler(), PacketInAPIPlayerTab.class)
                .registerHandler(new PacketInServiceConnect().getPacketUUID(), new PacketInServiceHandler(), PacketInServiceConnect.class)
                .registerHandler(new PacketInServiceDisconnect().getPacketUUID(), new PacketInServiceHandler(), PacketInServiceDisconnect.class)
                .registerHandler(new PacketInLaunchService().getPacketUUID(), new PacketInServiceHandler(), PacketInLaunchService.class)
                .registerHandler(new PacketInStopService().getPacketUUID(), new PacketInServiceHandler(), PacketInStopService.class)
                .registerHandler(new PacketLaunchServiceWithCustomTemplate().getPacketUUID(), new PacketInServiceHandler(), PacketLaunchServiceWithCustomTemplate.class)
                .registerHandler(new PacketInChangeState().getPacketUUID(), new PacketInServiceHandler(), PacketInChangeState.class)

                //COMMAND
                .registerHandler(new PacketInDispatchCommand().getPacketUUID(), new PacketInCommandHandler(), PacketInDispatchCommand.class)
                .registerHandler(new PacketInDispatchMainCommand().getPacketUUID(), new PacketInCommandHandler(), PacketInDispatchMainCommand.class)
                .registerHandler(new PacketOutAPIPlayerDispatchCommand().getPacketUUID(), new PacketInCommandHandler(), PacketOutAPIPlayerDispatchCommand.class)

                //GROUP
                .registerHandler(new PacketInStopGroup().getPacketUUID(), new PacketInGroupHandler(), PacketInStopGroup.class)
                .registerHandler(new PacketInCreateGroup().getPacketUUID(), new PacketInGroupHandler(), PacketInCreateGroup.class)
                .registerHandler(new PacketInDeleteGroup().getPacketUUID(), new PacketInGroupHandler(), PacketInDeleteGroup.class)

                //NODE
                .registerHandler(new PacketInAuthNode().getPacketUUID(), new PacketInNodeHandler(), PacketInAuthNode.class)
                .registerHandler(new PacketInNodeActionSuccess().getPacketUUID(), new PacketInNodeHandler(), PacketInNodeActionSuccess.class)
                .registerHandler(new PacketInShutdownNode().getPacketUUID(), new PacketInNodeHandler(), PacketInShutdownNode.class)
                .registerHandler(new PacketInSendConsole().getPacketUUID(), new PacketInNodeHandler(), PacketInSendConsole.class)
                .registerHandler(new PacketInSendConsoleFromNode().getPacketUUID(), new PacketInNodeHandler(), PacketInSendConsoleFromNode.class)

                //COMMAND
                .registerHandler(new PacketInCommandMaintenance().getPacketUUID(), new HandlePacketInCommand(), PacketInCommandMaintenance.class)
                .registerHandler(new PacketInCommandMaxPlayers().getPacketUUID(), new HandlePacketInCommand(), PacketInCommandMaxPlayers.class)
                .registerHandler(new PacketInCommandMinCount().getPacketUUID(), new HandlePacketInCommand(), PacketInCommandMinCount.class)
                .registerHandler(new PacketInCommandWhitelist().getPacketUUID(), new HandlePacketInCommand(), PacketInCommandWhitelist.class)

                //PLAYER
                .registerHandler(new PacketInPlayerConnect().getPacketUUID(), new PacketInPlayerHandler(), PacketInPlayerConnect.class)
                .registerHandler(new PacketInPlayerDisconnect().getPacketUUID(), new PacketInPlayerHandler(), PacketInPlayerDisconnect.class)
                .registerHandler(new PacketInPlayerSwitchService().getPacketUUID(), new PacketInPlayerHandler(), PacketInPlayerSwitchService.class)
                .registerHandler(new PacketInCloudPlayerComponent().getPacketUUID(), new PacketInPlayerHandler(), PacketInCloudPlayerComponent.class);

    }

    private void initCommands() {
        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("commands-load"));

        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new HelpCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new GroupCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new ClearCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new StopCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new ServiceCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new NodeCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new ReloadCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new PrexorCloudCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new QueueCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new PlayersCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new ScreenCommand());

        Driver.getInstance().getTerminalDriver().log(
                Type.INFO,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("commands-load-successfully")
                        .replace("%commands%", "" + Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().size())
        );
    }
}
