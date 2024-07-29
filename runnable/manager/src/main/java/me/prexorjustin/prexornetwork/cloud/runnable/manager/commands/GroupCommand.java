package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;
import java.util.Arrays;

@CommandInfo(command = "group", aliases = {"g", "template", "temp"}, description = "command-group-description")
public class GroupCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        String group = "";
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "create":
                        Driver.getInstance().getTerminalDriver().joinSetup();
                        break;

                    case "list":
                        if (Driver.getInstance().getGroupDriver().getAll().isEmpty()) {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found"));
                            return;
                        }

                        ArrayList<Group> groups = Driver.getInstance().getGroupDriver().getAll();
                        for (int i = 0; i != groups.size(); i++) {
                            Group raw = groups.get(i);
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, raw.getName() + "~" + raw.getGroupType() + " | " + raw.getStorage().getRunningNode());
                        }
                        break;

                    default:
                        sendHelp();
                }
                break;
            case 2:
                group = args[0];

                switch (args[1].toLowerCase()) {
                    case "delete":
                        if (Driver.getInstance().getGroupDriver().find(group)) {
                            Driver.getInstance().getGroupDriver().delete(group);
                            PrexorCloudManager.serviceDriver.delete.add(group);
                            PrexorCloudManager.serviceDriver.getServices(group).forEach(taskedService -> PrexorCloudManager.serviceDriver.unregister(taskedService.getEntry().getServiceName()));

                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-delete")
                                            .replace("%group%", group)
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                            .replace("%group%", group)
                            );
                        }
                        break;

                    case "info":
                        if (Driver.getInstance().getGroupDriver().find(group)) {
                            Group raw = Driver.getInstance().getGroupDriver().load(group);
                            Driver.getInstance().getTerminalDriver().log(Type.SUCCESS, new ConfigDriver().convert(raw));
                        } else {
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                            .replace("%group%", group)
                            );
                        }
                        break;
                    default:
                        sendHelp();
                        break;
                }
                break;

            case 4:
                group = args[0];
                if (args[1].equalsIgnoreCase("set")) {
                    switch (args[2].toLowerCase()) {
                        case "maintenance":
                            if (group.equals("--all")) {
                                Driver.getInstance().getGroupDriver().getAll().forEach(group1 -> {
                                    group1.setMaintenance(args[2].equalsIgnoreCase("true"));
                                    Driver.getInstance().getGroupDriver().update(group1);
                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + group1.getName(), new ConfigDriver().convert(group1));
                                });

                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-change-maintenance"));
                            } else if (Driver.getInstance().getGroupDriver().find(group)) {
                                Group raw = Driver.getInstance().getGroupDriver().load(group);
                                raw.setMaintenance(args[2].equalsIgnoreCase("true"));
                                Driver.getInstance().getGroupDriver().update(raw);
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-change-maintenance"));

                                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                            } else
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );

                            break;

                        case "template":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (Driver.getInstance().getTemplateDriver().get().contains(args[2].replace(" ", ""))) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.getStorage().setTemplate(args[2].replace(" ", ""));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-template-change"));
                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                                } else
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-no-template"));
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "minservercount":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].matches("[0-9]+")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setMinOnline(Integer.valueOf(args[2]));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-min-count"));

                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-only-number"));
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "maxservercount":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].matches("[0-9]+")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setMaxOnline(Integer.valueOf(args[2]));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-max-count"));

                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-only-number"));
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "priority":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].matches("[0-9]+")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setPriority(Integer.valueOf(args[2]));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-priority"));

                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-only-number"));
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "javaenvironment":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                Group raw = Driver.getInstance().getGroupDriver().load(group);
                                raw.getStorage().setJavaEnvironment(args[2]);
                                Driver.getInstance().getGroupDriver().update(raw);
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-environment")
                                                .replace("%path%", args[2])
                                );

                                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "maxplayer":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].matches("[0-9]+")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setMaxPlayer(Integer.valueOf(args[2]));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));

                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-max-players")
                                    );
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-only-number")
                                    );
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "startnewpercentage":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].matches("[0-9]+")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setStartNewPercentage(Integer.valueOf(args[2]));
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));

                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-start-percentage")
                                    );
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-only-number")
                                    );
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        case "permission":
                            if (Driver.getInstance().getGroupDriver().find(group)) {
                                if (args[2].contains(".")) {
                                    Group raw = Driver.getInstance().getGroupDriver().load(group);
                                    raw.setPermission(args[2]);
                                    Driver.getInstance().getGroupDriver().update(raw);
                                    Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));

                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-permission")
                                    );
                                } else {
                                    Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-need-dot")
                                    );
                                }
                            } else {
                                Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-not-found")
                                                .replace("%group%", group)
                                );
                            }
                            break;

                        default:
                            sendHelp();
                            break;
                    }
                }
            default:
                sendHelp();
                break;
        }
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> commands = new ArrayList<>();
        switch (args.length) {
            case 0:
                commands.addAll(Arrays.stream(SubCommands.values()).filter(subCommands -> subCommands.getArgsNeeded() == 0).map(SubCommands::getName).toList());
                Driver.getInstance().getGroupDriver().getAll().forEach(group -> commands.add(group.getName()));
                commands.add("--all");
                break;

            case 1:
                if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("list")) {
                    commands.addAll(Arrays.stream(SubCommands.values()).filter(subCommands -> subCommands.getArgsNeeded() == 1).map(SubCommands::getName).toList());
                    commands.add("set");
                }
                break;

            case 2:
                if (args[1].equalsIgnoreCase("set")) {
                    commands.addAll(Arrays.stream(SubCommands.values()).filter(subCommands -> subCommands.getArgsNeeded() == 2).map(SubCommands::getName).toList());
                }
                break;

            case 3:
                if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("list")) {
                    switch (args[2].toLowerCase()) {
                        case "maintenance":
                            commands.add("true, false");
                            break;

                        case "template":
                            commands.addAll(Driver.getInstance().getTemplateDriver().get());
                            break;
                    }
                }
                break;
        }

        return commands;
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-2"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-3"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-4"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-5"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-6"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-7"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-8"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-9"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-10"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-11"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-12"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-group-help-13")
        );
    }

    @AllArgsConstructor
    @Getter
    public enum SubCommands {
        CREATE("create", 0),
        LIST("list", 0),
        DELETE("delete", 1),
        INFO("info", 1),
        MAINTENANCE("maintenance", 2),
        MAX_PLAYER("maxplayer", 2),
        TEMPLATE("template", 2),
        MIN_SERVER_COUNT("minservercount", 2),
        MAX_SERVER_COUNT("maxservercount", 2),
        JAVA_ENVIRONMENT("javaenvironment", 2),
        PRIORITY("priority", 2),
        START_NEW_PERCENTAGE("startnewpercentage", 2),
        PERMISSION("permission", 2);

        private final String name;
        private final int argsNeeded;
    }
}
