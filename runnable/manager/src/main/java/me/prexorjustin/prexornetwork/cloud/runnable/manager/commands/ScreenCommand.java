package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;

@CommandInfo(command = "screen", description = "command-screen-description", aliases = {"terminal"})
public class ScreenCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        if (args.length == 2) {
            String name = args[1];
            if (args[0].equalsIgnoreCase("service")) {
                if (PrexorCloudManager.serviceDriver.getService(name) != null && PrexorCloudManager.serviceDriver.getService(name).getEntry().getServiceState() != ServiceState.QUEUED) {
                    PrexorCloudManager.serviceDriver.getService(name).handleScreen();
                } else {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.COMMAND,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-screen-name-not-found")
                    );
                }
            } else if (args[0].equalsIgnoreCase("node")) {
                if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(name)) {
                    PrexorCloudManager.screenNode(name);
                } else {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.COMMAND,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-screen-node-not-found")
                    );
                }
            } else sendHelp();
        } else sendHelp();
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> returns = new ArrayList<>();
        if (args.length == 0) {
            returns.add("service");
            returns.add("node");
        } else if (args[0].equalsIgnoreCase("service")) {
            returns.addAll(PrexorCloudManager.serviceDriver.getServices().stream().map(taskedService -> taskedService.getEntry().getServiceName()).toList());
        } else if (args[0].equalsIgnoreCase("node")) {
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
            config.getNodes().forEach(managerConfigNodes -> {
                if (!managerConfigNodes.getName().equalsIgnoreCase("InternalNode"))
                    returns.add(managerConfigNodes.getName());
            });
        }
        return returns;
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-screen-help-1"));
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-screen-help-2"));
    }
}
