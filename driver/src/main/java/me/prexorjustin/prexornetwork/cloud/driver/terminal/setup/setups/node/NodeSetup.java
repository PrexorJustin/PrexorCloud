package me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.node;

import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig.NodeConfig;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.classes.SetupClass;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class NodeSetup extends SetupClass {

    @Override
    public void call(String line) {
        if (getStep() == 0) {
            if (line.contains("/setup/")) {
                NodeConfig config = (NodeConfig) new ConfigDriver().convert(new RestDriver().getWithoutAuthentication(line), NodeConfig.class);
                new ConfigDriver("nodeservice.json").save(config);
                Driver.getInstance().getTerminalDriver().leaveSetup();
            } else {
                Driver.getInstance().getTerminalDriver().clearScreen();
                Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
                Driver.getInstance().getTerminalDriver().log(
                        Type.INSTALLATION,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-failed")
                );

                new TimerBase().scheduleAsync(new TimerTask() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        String ip = new BufferedReader(new InputStreamReader(URI.create("https://checkip.amazonaws.com").toURL().openConnection().getInputStream())).readLine();
                        Driver.getInstance().getTerminalDriver().clearScreen();
                        Driver.getInstance().getTerminalDriver().log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
                        Driver.getInstance().getTerminalDriver().log(
                                Type.INSTALLATION,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-node-question-1")
                                        .replace("%address%", ip).split("\n")
                        );
                    }
                }, 2, TimeUtil.SECONDS);
            }
        }
    }

    @Override
    public List<String> tabComplete() {
        return new ArrayList<>();
    }
}
