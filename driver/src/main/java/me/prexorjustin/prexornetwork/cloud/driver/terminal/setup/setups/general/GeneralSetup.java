package me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.general;

import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.classes.SetupClass;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.manager.ManagerSetup;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.node.NodeSetup;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class GeneralSetup extends SetupClass {

    @Override
    public void call(String line) {
        switch (getStep()) {
            case 0:
                if (Driver.getInstance().getLanguageDriver().getSupportedLanguages().stream().anyMatch(s -> s.equalsIgnoreCase(line))) {
                    Driver.getInstance().getMessageStorage().setLanguage(line.toUpperCase());
                    Driver.getInstance().getLanguageDriver().reload();

                    questionSuccess("language", line.toUpperCase(), 2, "yes, no");
                } else {
                    String joinedLanguages = String.join(", ", Driver.getInstance().getLanguageDriver().getSupportedLanguages());
                    questionFailed(1, joinedLanguages);
                }
                break;

            case 1:
                switch (line.toUpperCase()) {
                    case "YES", "Y ", "NO", "N":
                        questionSuccess("updater", line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("y"), 3, "Manager, Node");
                        break;
                    default:
                        questionFailed(2, "yes, no");
                        break;
                }
                break;

            case 2:
                switch (line.toUpperCase()) {
                    case "MANAGER":
                        startManagerSetup();
                        break;

                    case "NODE":
                        startNodeSetup();
                        break;

                    default:
                        questionFailed(3, "Manager, Node");
                        break;
                }
                break;
        }
    }

    @Override
    public List<String> tabComplete() {
        List<String> complete = new ArrayList<>();
        switch (getStep()) {
            case 0:
                complete.addAll(Driver.getInstance().getLanguageDriver().getSupportedLanguages());
                break;

            case 1:
                complete.add("yes");
                complete.add("no");
                break;

            case 2:
                complete.add("Manager");
                complete.add("Node");
                break;
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
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-" + nextStep)
        );

        if (possibleAnswers != null)
            Driver.getInstance().getTerminalDriver().log(
                    Type.INSTALLATION,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers")
                            .replace("%possible_answers%", possibleAnswers)
            );
    }

    private void startManagerSetup() {
        Driver.getInstance().getTerminalDriver().clearScreen();
        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
        Driver.getInstance().getTerminalDriver().log(
                Type.INSTALLATION,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-manager-question-1")
        );
        Driver.getInstance().getTerminalDriver().getSetupDriver().setSetup(new ManagerSetup());
        Driver.getInstance().getTerminalDriver().getSetupDriver().getSetup().getAnswers().putAll(getAnswers());
    }

    @SneakyThrows
    private void startNodeSetup() {
        String ip = new BufferedReader(new InputStreamReader(URI.create("https://checkip.amazonaws.com").toURL().openConnection().getInputStream())).readLine();

        Driver.getInstance().getTerminalDriver().clearScreen();
        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
        Driver.getInstance().getTerminalDriver().log(
                Type.INSTALLATION,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-node-question-1")
                        .replace("%address%", ip)
        );
        Driver.getInstance().getTerminalDriver().getSetupDriver().setSetup(new NodeSetup());
        Driver.getInstance().getTerminalDriver().getSetupDriver().getSetup().getAnswers().putAll(getAnswers());
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
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-" + step)
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
