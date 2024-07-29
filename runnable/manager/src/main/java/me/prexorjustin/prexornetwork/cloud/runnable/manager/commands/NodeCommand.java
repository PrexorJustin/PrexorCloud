package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfigNodes;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig.NodeConfig;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;


@CommandInfo(command = "node", description = "command-node-description", aliases = {"nodes", "cluster", "wrapper"})
public class NodeCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        switch (args.length) {
            case 1 -> {
                if (args[0].equalsIgnoreCase("list")) {
                    PrexorCloudManager.config.getNodes().forEach(managerConfigNodes -> {
                        if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(managerConfigNodes.getName()) || managerConfigNodes.getName().equalsIgnoreCase("InternalNode")) {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    "§f" + managerConfigNodes.getName() + "~" + managerConfigNodes.getAddress() + "-ONLINE"
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    "§f" + managerConfigNodes.getName() + "~" + managerConfigNodes.getAddress() + "-OFFLINE"
                            );
                        }
                    });
                } else {
                    sendHelp();
                }
            }

            case 2 -> {
                String node = args[1];
                switch (args[0]) {
                    case "delete" -> {
                        if (PrexorCloudManager.config.getNodes().stream().anyMatch(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node))) {
                            PrexorCloudManager.config.getNodes().removeIf(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node));
                            new ConfigDriver("./service.json").save(PrexorCloudManager.config);

                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-delete")
                                            .replace("%node%", node)
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-not-found")
                                            .replace("%node%", node)
                            );
                        }
                    }

                    case "services" -> {
                        if (PrexorCloudManager.config.getNodes().stream().anyMatch(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node))) {
                            PrexorCloudManager.serviceDriver.getServicesFromNode(node).forEach(taskedService ->
                                    Driver.getInstance().getTerminalDriver().log(
                                            Type.COMMAND,
                                            taskedService.getEntry().getServiceName() + "~" + taskedService.getEntry().getCurrentPlayers()
                                    ));

                            PrexorCloudManager.config.getNodes().removeIf(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node));
                            new ConfigDriver("./service.json").save(PrexorCloudManager.config);
                        } else {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-not-found")
                                            .replace("%node%", node)
                            );
                        }
                    }

                    default -> sendHelp();
                }
            }

            case 3 -> {

                if (args[0].equalsIgnoreCase("create")) {
                    String node = args[1];
                    String address = args[2];
                    if (PrexorCloudManager.config.getNodes().stream().noneMatch(managerConfigNodes -> managerConfigNodes.getName().equalsIgnoreCase(node))) {
                        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-create")
                                        .replace("%node%", node)
                        );

                        ManagerConfigNodes nodes = new ManagerConfigNodes();
                        nodes.setName(node);
                        nodes.setAddress(address);
                        PrexorCloudManager.config.getNodes().add(nodes);

                        String link = "http://" + PrexorCloudManager.config.getManagerAddress() + ":" + PrexorCloudManager.config.getRestPort() + "/setup/" + node;
                        NodeConfig config = new NodeConfig();
                        config.setLanguage(PrexorCloudManager.config.getLanguage());
                        config.setManagerAddress(PrexorCloudManager.config.getManagerAddress());
                        config.setCanUseMemory(1024);
                        config.setBungeeVersion(PrexorCloudManager.config.getBungeeVersion());
                        config.setSpigotVersion(PrexorCloudManager.config.getSpigotVersion());
                        config.setNetworkingPort(PrexorCloudManager.config.getNetworkingPort());
                        config.setRestPort(PrexorCloudManager.config.getRestPort());
                        config.setCopyLogs(PrexorCloudManager.config.isCopyLogs());
                        config.setBungeePort(PrexorCloudManager.config.getBungeePort());
                        config.setProcessorUsage(PrexorCloudManager.config.getProcessorUsage());
                        config.setAutoUpdate(PrexorCloudManager.config.isAutoUpdate());
                        config.setSpigotPort(PrexorCloudManager.config.getSpigotPort());
                        config.setNodeAddress(address);
                        config.setNodeName(node);

                        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/setup/" + node, new ConfigDriver().convert(config)));
                        new ConfigDriver("./service.json").save(PrexorCloudManager.config);

                        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-link")
                                        .replace("%link%", link)
                        );
                    } else {
                        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-exists")
                                        .replace("%node%", node)
                        );
                    }
                } else sendHelp();
            }

            default -> sendHelp();
        }
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> commands = new ArrayList<>();
        if (args.length == 0) {
            commands.add("create");
            commands.add("delete");
            commands.add("services");
            commands.add("list");
        } else if (args.length == 1 & !args[0].equalsIgnoreCase("list")) {
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
            config.getNodes().forEach(managerConfigNodes -> commands.add(managerConfigNodes.getName()));
        }
        return commands;
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-help-2"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-help-3"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-node-help-4")
        );
    }
}
