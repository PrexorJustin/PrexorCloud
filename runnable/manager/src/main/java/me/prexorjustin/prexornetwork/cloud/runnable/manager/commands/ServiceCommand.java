package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist.Whitelist;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerKick;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedEntry;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandInfo(command = "service", description = "command-service-description", aliases = {"s", "serv", "task", "start"})
public class ServiceCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        switch (args.length) {
            case 0 -> sendHelp();

            case 1 -> {
                if (args[0].equalsIgnoreCase("list")) {
                    if (PrexorCloudManager.serviceDriver.getServices().isEmpty()) {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-no-services")
                        );
                        return;
                    }

                    PrexorCloudManager.config.getNodes().stream().filter(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase("InternalNode") || NettyDriver.getInstance().getNettyServer().isChannelRegistered(managerConfigNodes.getName())).forEach(managerConfigNodes -> {
                        Driver.getInstance().getTerminalDriver().log(Type.COMMAND, managerConfigNodes.getName() + ": ");
                        PrexorCloudManager.serviceDriver.getServices().stream().filter(taskedService -> taskedService.getEntry().getTaskNode().equalsIgnoreCase(managerConfigNodes.getName())).forEach(taskedService -> {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    " > " + taskedService.getEntry().getServiceName()
                                            + "~" + taskedService.getEntry().getServiceState().toString()
                                            + "-" + taskedService.getEntry().getUsedPort()
                                            + " §r(players: §f" + taskedService.getEntry().getCurrentPlayers() + "§r) "
                            );
                        });
                    });
                } else sendHelp();
            }

            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "stop" -> {
                        String service = args[1];
                        if (PrexorCloudManager.serviceDriver.getService(service) != null) {
                            PrexorCloudManager.serviceDriver.unregister(service);
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    }

                    case "stopgroup" -> {
                        String group = args[1];
                        if (Driver.getInstance().getGroupDriver().find(group)) {
                            PrexorCloudManager.serviceDriver.getServices(group).forEach(taskedService -> PrexorCloudManager.serviceDriver.unregister(taskedService.getEntry().getServiceName()));
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-group-not-found")
                                            .replace("%group%", group)
                            );

                        }
                    }

                    case "restart" -> {
                        String service = args[1];
                        if (PrexorCloudManager.serviceDriver.getService(service) != null) {
                            PrexorCloudManager.serviceDriver.getService(service).handleRestart();
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    }

                    case "restartgroup" -> {
                        String group = args[1];
                        if (Driver.getInstance().getGroupDriver().load(group) != null) {
                            PrexorCloudManager.serviceDriver.getServices(group).forEach(TaskedService::handleRestart);

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-group-restart")
                                            .replace("%group%", group)
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-group-not-found")
                                            .replace("%group%", group)
                            );
                        }
                    }

                    case "restartnode" -> {
                        String node = args[1];
                        ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
                        if (config.getNodes().stream().anyMatch(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node))) {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-group-not-found")
                                            .replace("%node%", node)
                            );

                            PrexorCloudManager.serviceDriver.getServicesFromNode(node).forEach(TaskedService::handleRestart);
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-node-restart")
                                            .replace("%node%", node)
                            );
                        }
                    }

                    case "copy" -> {
                        String service = args[1];
                        if (PrexorCloudManager.serviceDriver.getService(service) != null) {
                            PrexorCloudManager.serviceDriver.getService(service).handleSync();

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-sync")
                                            .replace("%service%", service)
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    }

                    case "info" -> {
                        String service = args[1];
                        if (PrexorCloudManager.serviceDriver.getService(service) != null) {
                            TaskedService taskedService = PrexorCloudManager.serviceDriver.getService(service);
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service name: §f" + taskedService.getEntry().getServiceName()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service uuid: §f" + taskedService.getEntry().getUuid()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service Node: §f" + taskedService.getEntry().getTaskNode()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service group: §f" + taskedService.getEntry().getGroupName()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service Port: §f" + taskedService.getEntry().getUsedPort()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service Players: §f" + taskedService.getEntry().getCurrentPlayers()
                            );
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service Status: §f" + taskedService.getEntry().getServiceState()
                            );
                            int time = Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - taskedService.getEntry().getTime())));
                            Driver.getInstance().getTerminalDriver().log(Type.SERVICE,
                                    "Service Uptime: §f" + time + " Second(s)"
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    }
                }
            }

            case 3 -> {
                if (args[0].equalsIgnoreCase("whitelist")) {
                    if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
                        String username = args[2];
                        boolean add = args[1].equalsIgnoreCase("add");

                        if (!PrexorCloudManager.config.getWhitelist().contains(username))
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-player-not-found")
                                            .replace("%player%", username)
                            );

                        if (add) PrexorCloudManager.config.getWhitelist().add(username);
                        else PrexorCloudManager.config.getWhitelist().remove(username);

                        new ConfigDriver("./service.json").save(PrexorCloudManager.config);
                        Whitelist whitelistConfig = new Whitelist();
                        whitelistConfig.setWhitelist(PrexorCloudManager.config.getWhitelist());
                        Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.WHITELIST.getRoute(), new ConfigDriver().convert(whitelistConfig));
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND, add ? Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-player-add-whitelist").replace("%player%", username)
                                        : Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-player-remove-whitelist").replace("%player%", username)
                        );

                        if (!add) {
                            PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(WebServer.Routes.PLAYER_GENERAL.getRoute()), PlayerGeneral.class);
                            if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(username))) {
                                NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(
                                        new PacketOutAPIPlayerKick(username, ((Messages) new ConfigDriver("./local/messages.json").read(Messages.class)).getMessages().get("kickNetworkIsMaintenance"))
                                );
                            }
                        }
                    } else sendHelp();
                } else if (args[0].equalsIgnoreCase("run")) {
                    String group = args[1];

                    if (Driver.getInstance().getGroupDriver().find(group)) {
                        if (args[2].matches("[0-9]+")) {
                            Group gdata = Driver.getInstance().getGroupDriver().load(group);

                            if (gdata.getMaxOnline() != -1) {
                                if (gdata.getMaxOnline() < (PrexorCloudManager.serviceDriver.getServices(group).size() + Integer.parseInt(args[2]))) {
                                    Driver.getInstance().getTerminalDriver().log(
                                            Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-max-services")
                                    );
                                    return;
                                }
                            }

                            for (int i = 0; i != Integer.parseInt(args[2]); i++) {
                                String id = "";
                                if (PrexorCloudManager.config.getUuid().equals("INT"))
                                    id = String.valueOf(PrexorCloudManager.serviceDriver.getFreeUUID(group));
                                else if (PrexorCloudManager.config.getUuid().equals("RANDOM"))
                                    id = PrexorCloudManager.serviceDriver.getFreeUUID();

                                PrexorCloudManager.serviceDriver.register(new TaskedEntry(
                                        PrexorCloudManager.serviceDriver.getFreePort(gdata.getGroupType().equalsIgnoreCase("PROXY")),
                                        gdata.getName(),
                                        gdata.getName() + PrexorCloudManager.config.getSplitter() + id,
                                        gdata.getStorage().getRunningNode(),
                                        PrexorCloudManager.config.isUseProtocol(),
                                        id,
                                        false,
                                        ""
                                ));

                                Driver.getInstance().getMessageStorage().setCanUseMemory(Driver.getInstance().getMessageStorage().getCanUseMemory() - gdata.getUsedMemory());
                            }
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-number")
                            );
                        }
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-group-not-found")
                                        .replace("%group%", group)
                        );
                    }
                }
            }
            default -> {
                if (args[0].equalsIgnoreCase("execute")) {
                    StringBuilder msg = new StringBuilder();
                    String service = args[1];

                    for (int i = 2; i < args.length; i++) msg.append(args[i]).append(" ");

                    if (PrexorCloudManager.serviceDriver.getService(service) != null && NettyDriver.getInstance().getNettyServer().isChannelRegistered(service) || service.equalsIgnoreCase("--all")) {
                        if (service.equalsIgnoreCase("--all")) {
                            PrexorCloudManager.serviceDriver.getServices().forEach(taskedService -> taskedService.handleExecute(msg.toString()));

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-send-command-all")
                            );
                        } else {
                            PrexorCloudManager.serviceDriver.getService(service).handleExecute(msg.toString());

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-send-command")
                                        .replace("%service%", service)
                        );
                    }
                } else sendHelp();
            }
        }
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> commands = new ArrayList<>();

        switch (args.length) {
            case 0 -> commands.addAll(Arrays.stream(SubCommands.values()).map(SubCommands::getName).toList());
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    default -> {
                        PrexorCloudManager.serviceDriver.getServices().forEach(taskedService -> commands.add(taskedService.getEntry().getServiceName()));
                        if (args[0].equalsIgnoreCase("execute") || args[0].equalsIgnoreCase("copy")) {
                            commands.add("--all");
                        }
                    }

                    case "whitelist" -> commands.addAll(List.of("add", "remove"));
                    case "run" -> commands.addAll(Driver.getInstance().getGroupDriver().getAllStrings());
                    case "restartnode" ->
                            ((ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class)).getNodes().forEach(managerConfigNodes -> commands.add(managerConfigNodes.getName()));
                }
            }

            case 2 -> {
                if (args[0].equalsIgnoreCase("whitelist") && args[1].equalsIgnoreCase("remove"))
                    commands.addAll(PrexorCloudManager.config.getWhitelist());
            }
        }

        return commands;
    }


    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-2"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-3"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-4"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-5"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-6"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-7"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-8"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-9"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-10"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-help-11")
        );
    }

    @AllArgsConstructor
    @Getter
    public enum SubCommands {
        LIST("list", 0),
        STOP("stop", 1),
        STOP_GROUP("stopgroup", 1),
        RESTART("restart", 1),
        RESTART_GROUP("restartgroup", 1),
        RESTART_NODE("restartnode", 1),
        COPY("copy", 1),
        INFO("info", 1),
        WHITELIST("whitelist", 2),
        RUN("run", 2),
        EXECUTE("execute", -1);

        private final String name;
        private final int argsNeeded;
    }
}
