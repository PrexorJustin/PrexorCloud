package me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.group;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfigNodes;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.GroupStorage;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.classes.SetupClass;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GroupSetup extends SetupClass {

    @Override
    public void call(String line) {
        switch (getStep()) {
            case 0 -> questionSuccess("group", line, 2, "PROXY, LOBBY, GAME");

            case 1 -> {
                Driver.getInstance().getTerminalDriver().clearScreen();

                switch (line.toUpperCase()) {
                    case "PROXY", "PROXY ":
                        questionSuccess("groupType", "PROXY", 3, null);
                        break;
                    case "LOBBY", "LOBBY ":
                        questionSuccess("groupType", "LOBBY", 3, null);
                        break;
                    case "GAME", "GAME ":
                        questionSuccess("groupType", "GAME", 3, null);
                        break;
                    default:
                        questionFailed(2, "PROXY, LOBBY, GAME");
                        break;
                }
            }

            case 2 -> {
                if (line.matches("[0-9]+")) {
                    questionSuccess("memory", line.replace(" ", ""), 4, "YES, NO");
                } else {
                    questionFailed(3, null);
                }
            }

            case 3 -> {
                switch (line.toUpperCase()) {
                    case "YES", "YES ", "Y", "Y ":
                        questionSuccess("static", true, 5, null);
                        break;
                    case "NO", "NO ", "N", "N ":
                        questionSuccess("static", false, 5, null);
                    default:
                        questionFailed(4, "yes, no");
                        break;
                }
            }

            case 4 -> {
                if (line.matches("[0-9]+")) {
                    questionSuccess("players", line, 6, null);
                } else {
                    questionFailed(5, null);
                }
            }

            case 5 -> {
                if (line.matches("[0-9]+")) {
                    questionSuccess("minOnline", line, 7, null);
                } else {
                    questionFailed(6, null);
                }
                break;
            }

            case 6 -> {
                if (line.matches("[0-9]+") || line.equalsIgnoreCase("-1")) {
                    questionSuccess("maxOnline", line, 8, null);
                } else {
                    questionFailed(7, null);
                }
            }

            case 7 -> {
                if (line.matches("[0-9]+") && Integer.parseInt(line) <= 100) {
                    questionSuccess("startNew", Integer.valueOf(line), 9, null);
                } else {
                    questionFailed(8, null);
                }
            }

            case 8 -> {
                if (line.matches("[0-9]+")) {
                    questionSuccess("group100", Integer.valueOf(line), 10, null);
                } else {
                    questionFailed(9, null);
                }
            }

            case 9 -> {
                if (line.matches("[0-9]+")) {
                    questionSuccess("network100", Integer.valueOf(line), 11, this.getTemplateListAsString());
                } else {
                    questionFailed(10, null);
                }
            }

            case 10 -> {
                ArrayList<String> rawTemplates = Driver.getInstance().getTemplateDriver().get();
                ArrayList<String> templates = new ArrayList<>();
                rawTemplates.forEach(s -> {
                    templates.add(s);
                    templates.add(s + " ");
                });

                if (templates.contains(line) || line.equalsIgnoreCase("CREATE") || line.equals("CREATE ")) {
                    ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
                    ArrayList<ManagerConfigNodes> configNodes = config.getNodes();
                    StringBuilder templateListBuilder = new StringBuilder();

                    for (int i = 0; i != configNodes.size(); i++) {
                        String temp = configNodes.get(i).getName();
                        if (i == configNodes.size() - 1) {
                            templateListBuilder.append(temp);
                        } else {
                            templateListBuilder.append(temp).append(", ");
                        }
                    }

                    questionSuccess("template", line.replace(" ", "").replace("CREATE", getAnswers().get("group").toString()), 12, templateListBuilder.toString());
                } else {
                    questionFailed(11, this.getTemplateListAsString());
                }
            }

            case 11 -> {
                Driver.getInstance().getTerminalDriver().clearScreen();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Driver.getInstance().getGroupDriver().create(new Group(
                                getAnswers().get("group").toString(),
                                getAnswers().get("groupType").toString(),
                                Integer.valueOf(getAnswers().get("memory").toString()),
                                true,
                                Boolean.parseBoolean(getAnswers().get("static").toString()),
                                0,
                                0,
                                Integer.valueOf(getAnswers().get("players").toString()),
                                Integer.valueOf(getAnswers().get("minonline").toString()),
                                Integer.valueOf(getAnswers().get("maxonline").toString()),
                                Integer.valueOf(getAnswers().get("startnew").toString()),
                                Integer.valueOf(getAnswers().get("group100").toString()),
                                Integer.valueOf(getAnswers().get("network100").toString()),
                                "",
                                new GroupStorage(
                                        getAnswers().get("template").toString(),
                                        getAnswers().get("node").toString(),
                                        "",
                                        ""
                                )
                        ));

                        Driver.getInstance().getTerminalDriver().leaveSetup();
                    }
                }, 1000);

                Driver.getInstance().getTerminalDriver().log(
                        Type.EMPTY,
                        Driver.getInstance().getMessageStorage().getAsciiArt()
                );
                Driver.getInstance().getTerminalDriver().log(
                        Type.INSTALLATION,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-group-finish")
                );

                getAnswers().put("node", line);
            }
        }
    }

    @Override
    public List<String> tabComplete() {
        List<String> complete = new ArrayList<>();
        switch (getStep()) {
            case 1:
                complete.add("PROXY");
                complete.add("GAME");
                complete.add("LOBBY");
                break;
            case 2:
                complete.add("512");
                complete.add("1024");
                complete.add("2048");
                break;
            case 3:
                complete.add("yes");
                complete.add("no");
                break;
            case 6:
                complete.add("-1");
                break;
            case 7:
                complete.add("25");
                complete.add("50");
                complete.add("75");
                complete.add("100");
                break;
            case 10:
                complete.addAll(Driver.getInstance().getTemplateDriver().get());
                complete.add("create");
                break;
            case 11:
                ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
                config.getNodes().forEach(managerConfigNodes -> complete.add(managerConfigNodes.getName()));
                break;
        }

        return complete;
    }

    private String getTemplateListAsString() {
        ArrayList<String> templates = Driver.getInstance().getTemplateDriver().get();
        String templateList;

        if (templates.isEmpty()) {
            templateList = "CREATE";
        } else {
            StringBuilder templateListBuilder = new StringBuilder();
            for (int i = 0; i != templates.size(); i++) {
                String temp = templates.get(i);
                templateListBuilder.append(temp.replace("null", "")).append(", ");
            }
            templateList = templateListBuilder.toString();
            templateList = templateList + "CREATE";
        }
        return templateList;
    }

    private void questionSuccess(String stepKey, Object stepValue, int nextStep, @Nullable String possibleAnswers) {
        Driver.getInstance().getTerminalDriver().clearScreen();

        addStep();
        getAnswers().put(stepKey, stepValue);

        Driver.getInstance().getTerminalDriver().log(
                Type.EMPTY,
                Driver.getInstance().getMessageStorage().getAsciiArt()
        );
        Driver.getInstance().getTerminalDriver().log(
                Type.INSTALLATION,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-group-question-" + nextStep)
        );

        if (possibleAnswers != null)
            Driver.getInstance().getTerminalDriver().log(
                    Type.INSTALLATION,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers")
                            .replace("%possible_answers%", possibleAnswers)
            );

    }

    private void questionFailed(int step, @Nullable String replacements) {
        Driver.getInstance().getTerminalDriver().clearScreen();
        Driver.getInstance().getTerminalDriver().log(
                Type.EMPTY,
                Driver.getInstance().getMessageStorage().getAsciiArt()
        );
        Driver.getInstance().getTerminalDriver().log(
                Type.INSTALLATION,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-failed")
        );

        new TimerBase().scheduleAsync(new TimerTask() {
            @Override
            public void run() {
                Driver.getInstance().getTerminalDriver().clearScreen();
                Driver.getInstance().getTerminalDriver().log(
                        Type.EMPTY,
                        Driver.getInstance().getMessageStorage().getAsciiArt()
                );
                Driver.getInstance().getTerminalDriver().log(
                        Type.INSTALLATION,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-group-question-" + step)
                );
                Driver.getInstance().getTerminalDriver().log(
                        Type.INSTALLATION, replacements == null ?
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers") :
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers")
                                        .replace("%possible_answers%", replacements)
                );
            }
        }, 2, TimeUtil.SECONDS);
    }
}
