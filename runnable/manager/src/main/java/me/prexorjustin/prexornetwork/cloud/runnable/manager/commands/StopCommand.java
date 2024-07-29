package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@CommandInfo(command = "stop", description = "command-stop-description", aliases = {"shutdown", "end", "quit", "kill"})
public class StopCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        if (Driver.getInstance().getMessageStorage().isShutdownAccept()) {
            PrexorCloudManager.shutdownHook();
        } else {
            Driver.getInstance().getMessageStorage().setShutdownAccept(true);
            Driver.getInstance().getTerminalDriver().log(
                    Type.COMMAND,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-stop")
            );
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Driver.getInstance().getMessageStorage().setShutdownAccept(false);
                }
            }, 15 * 1000);
        }
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        return null;
    }
}
