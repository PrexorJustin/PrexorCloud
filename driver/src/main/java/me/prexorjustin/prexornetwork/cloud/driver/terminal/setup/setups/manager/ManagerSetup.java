package me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.manager;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfigNodes;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.GroupStorage;
import me.prexorjustin.prexornetwork.cloud.driver.storage.PacketLoader;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.classes.SetupClass;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TimerTask;

public class ManagerSetup extends SetupClass {

    private String spigot;

    @Override
    public void call(String line) {
        List<String> spigots;

        switch (getStep()) {
            case 0:
                if (line.contains(".") && line.matches("[0-9.]+"))
                    questionSuccess("address", line, 2, null);
                else questionFailed(1, null);
                break;
            case 1:
                if (line.matches("[0-9]+")) {
                    questionSuccess("memory", Integer.valueOf(line), 3, this.getAvailableBungeecords());
                } else {
                    questionFailed(2, null);
                }
                break;

            case 2:
                if (new PacketLoader().getAvailableBungeecords().contains(line)) {
                    questionSuccess("bungee", line.toUpperCase(), 4, this.getAvailableSpigotPorts());
                } else {
                    questionFailed(3, this.getAvailableBungeecords());
                }
                break;

            case 3:
                spigots = new PacketLoader().getAvailableSpigots();
                List<String> mainSpigots = new ArrayList<>();
                spigots.forEach(s -> {
                    if (!mainSpigots.contains(s.split("-")[0])) {
                        mainSpigots.add(s.split("-")[0]);
                    }
                });

                if (mainSpigots.contains(line)) {
                    List<String> mainSpigots2 = new ArrayList<>();
                    spigots.forEach(s -> {
                        if (s.startsWith(line)) {
                            mainSpigots2.add(s);
                        }
                    });

                    StringBuilder available = new StringBuilder();
                    if (mainSpigots2.size() == 1) {
                        available = new StringBuilder(mainSpigots2.get(0));
                    } else {
                        for (int i = 0; i != mainSpigots2.size(); i++) {
                            if (i == 0) {
                                available = new StringBuilder(mainSpigots2.get(0));
                            } else {
                                available.append(", ").append(mainSpigots2.get(i));
                            }
                        }
                    }

                    addStep();
                    spigot = line;
                    Driver.getInstance().getTerminalDriver().clearScreen();
                    Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
                    Driver.getInstance().getTerminalDriver().log(
                            Type.INSTALLATION,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-manager-question-5")
                    );
                    Driver.getInstance().getTerminalDriver().log(
                            Type.INSTALLATION,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers")
                                    .replace("%possible_answers%", available)
                    );
                } else {
                    questionFailed(4, this.getAvailableSpigotPorts());
                }
                break;

            case 4:
                spigots = new PacketLoader().getAvailableSpigots();
                List<String> mainSpigots2 = new ArrayList<>();
                spigots.forEach(s -> {
                    if (s.startsWith(spigot)) mainSpigots2.add(s);
                });

                if (mainSpigots2.contains(line)) {
                    questionSuccess("spigot", line.toUpperCase(), 6, "yes, no");
                } else {
                    StringBuilder available = new StringBuilder();
                    if (mainSpigots2.size() == 1) available = new StringBuilder(mainSpigots2.get(0));
                    else {
                        for (int i = 0; i != mainSpigots2.size(); i++) {
                            if (i == 0) available = new StringBuilder(mainSpigots2.get(0));
                            else available.append(", ").append(mainSpigots2.get(i));
                        }
                    }

                    questionFailed(5, available.toString());
                }
                break;

            case 5:
                switch (line.toUpperCase()) {
                    case "YES", "Y", "NO", "N":
                        questionSuccess("players", line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("y"), 7, "yes, no");
                        break;
                    default:
                        questionFailed(6, "yes, no");
                        break;
                }
                break;

            case 6:
                switch (line.toUpperCase()) {
                    case "YES", "Y", "NO", "N":
                        addStep();
                        getAnswers().put("groups", line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("y"));

                        Driver.getInstance().getTerminalDriver().clearScreen();
                        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
                        Driver.getInstance().getTerminalDriver().log(
                                Type.INSTALLATION,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-manager-finish")
                        );

                        new TimerBase().scheduleAsync(new TimerTask() {
                            @Override
                            public void run() {
                                ManagerConfig managerConfig = new ManagerConfig();
                                ManagerConfigNodes managerConfigNodes = new ManagerConfigNodes();

                                managerConfigNodes.setName("InternalNode");
                                managerConfigNodes.setAddress(((String) getAnswers().get("address")));

                                managerConfig.setManagerAddress(managerConfigNodes.getAddress());
                                managerConfig.setLanguage(((String) getAnswers().get("language")));
                                managerConfig.setCanUseMemory(((Integer) getAnswers().get("memory")));
                                managerConfig.setBungeeVersion(((String) getAnswers().get("bungee")));
                                managerConfig.setSpigotVersion(((String) getAnswers().get("spigot")));
                                managerConfig.setNetworkingPort(7002);
                                managerConfig.setSplitter("-");
                                managerConfig.setUseProtocol(false);
                                managerConfig.setCopyLogs(true);
                                managerConfig.setProcessorUsage(90);
                                managerConfig.setTimeoutCheck(120);
                                managerConfig.setServiceStartupCount(4);
                                managerConfig.setRestPort(8097);
                                managerConfig.setShowConnectingPlayers((Boolean) getAnswers().get("players"));
                                managerConfig.setUuid("INT");
                                managerConfig.setBungeePort(25565);
                                managerConfig.setSpigotPort(5000);
                                managerConfig.setAutoUpdate((Boolean) getAnswers().get("updater"));
                                managerConfig.setWhitelist(new HashSet<>());

                                ArrayList<ManagerConfigNodes> nodes = new ArrayList<>();
                                nodes.add(managerConfigNodes);
                                managerConfig.setNodes(nodes);

                                new ConfigDriver("./service.json").save(managerConfig);

                                if ((boolean) getAnswers().get("groups")) {
                                    new File("./local/groups/").mkdirs();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Driver.getInstance().getGroupDriver().create(
                                            new Group("Proxy", "PROXY", 256, true, false, 0, 0, 512, 1, -1, 90, 1, 1, "", new GroupStorage("Proxy", "InternalNode", "", ""))
                                    );
                                    Driver.getInstance().getGroupDriver().create(
                                            new Group("Lobby", "LOBBY", 1024, false, false, 0, 1, 50, 1, -1, 90, 3, 3, "", new GroupStorage("Lobby", "InternalNode", "", ""))
                                    );
                                }

                                Driver.getInstance().getTerminalDriver().leaveSetup();
                            }
                        }, 5, TimeUtil.SECONDS);
                        break;
                    default:
                        questionFailed(7, "yes, no");
                        break;
                }
                break;
        }
    }

    @Override
    public List<String> tabComplete() {
        List<String> complete = new ArrayList<>();
        if (getStep() == 0) {
            try {
                String ip = new BufferedReader(new InputStreamReader(URI.create("https://checkip.amazonaws.com").toURL().openConnection().getInputStream())).readLine();
                complete.add("127.0.0.1");
                complete.add(ip);
            } catch (IOException ignored) {
            }

        } else if (getStep() == 1) {
            complete.add("1024");
            complete.add("" + 1024 * 2);
            complete.add("" + 1024 * 3);
            complete.add("" + 1024 * 4);
            complete.add("" + 1024 * 5);
            complete.add("" + 1024 * 10);
            complete.add("" + 1024 * 15);

        } else if (getStep() == 2) {
            return new PacketLoader().getAvailableBungeecords();
        } else if (getStep() == 3) {
            List<String> spigots = new PacketLoader().getAvailableSpigots();
            List<String> mainSpigots = new ArrayList<>();
            spigots.forEach(s -> {
                if (!mainSpigots.contains(s.split("-")[0])) {
                    mainSpigots.add(s.split("-")[0]);
                }
            });
            return mainSpigots;
        } else if (getStep() == 4) {
            List<String> spigots = new PacketLoader().getAvailableSpigots();
            List<String> mainSpigots2 = new ArrayList<>();
            spigots.forEach(s -> {
                if (s.startsWith(spigot)) {
                    mainSpigots2.add(s);
                }
            });

            return mainSpigots2;
        } else if (getStep() == 5) {
            complete.add("yes");
            complete.add("no");
        } else if (getStep() == 6) {
            complete.add("yes");
            complete.add("no");
        }
        return complete;
    }

    private void questionSuccess(String stepKey, Object stepValue, int nextStep, @Nullable String possibleAnswers) {
        Driver.getInstance().getTerminalDriver().clearScreen();

        addStep();
        getAnswers().put(stepKey, stepValue);

        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
        Driver.getInstance().getTerminalDriver().log(
                Type.INSTALLATION,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-manager-question-" + nextStep)
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
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-manager-question-" + step)
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

    private String getAvailableBungeecords() {
        List<String> availableBungeecords = new PacketLoader().getAvailableBungeecords();
        StringBuilder available = new StringBuilder();
        if (availableBungeecords.size() == 1) available = new StringBuilder(availableBungeecords.get(0));
        else {
            for (int i = 0; i != availableBungeecords.size(); i++) {
                if (i == 0) available = new StringBuilder(availableBungeecords.get(0));
                else available.append(", ").append(availableBungeecords.get(i));
            }
        }

        return available.toString();
    }

    private String getAvailableSpigotPorts() {
        List<String> availableSpigots = new PacketLoader().getAvailableSpigots();
        List<String> mainSpigots = new ArrayList<>();

        availableSpigots.forEach(s -> {
            if (!mainSpigots.contains(s.split("-")[0])) mainSpigots.add(s.split("-")[0]);
        });

        StringBuilder available = new StringBuilder();
        if (mainSpigots.size() == 1) available = new StringBuilder(mainSpigots.get(0));
        else {
            for (int i = 0; i != mainSpigots.size(); i++) {
                if (i == 0) available = new StringBuilder(mainSpigots.get(0));
                else available.append(", ").append(mainSpigots.get(i));
            }
        }

        return available.toString();
    }

    private String getAvailableSpigotPortVersions(String spigotPort) {
        List<String> availableSpigots = new PacketLoader().getAvailableSpigots();
        List<String> mainSpigots = new ArrayList<>();
        availableSpigots.forEach(s -> {
            if (!mainSpigots.contains(s.split("-")[0])) mainSpigots.add(s.split("-")[0]);
        });

        List<String> portVersions = new ArrayList<>();
        availableSpigots.forEach(s -> {
            if (s.startsWith(spigotPort)) {
                portVersions.add(s);
            }
        });

        StringBuilder available = new StringBuilder();
        if (portVersions.size() == 1) {
            available = new StringBuilder(portVersions.get(0));
        } else {

            for (int i = 0; i != portVersions.size(); i++) {
                if (i == 0) {
                    available = new StringBuilder(portVersions.get(0));
                } else {
                    available.append(", ").append(portVersions.get(i));
                }
            }
        }

        return available.toString();
    }
}
