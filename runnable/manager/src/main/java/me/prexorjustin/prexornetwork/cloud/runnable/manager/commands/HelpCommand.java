package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;

import java.util.ArrayList;

@CommandInfo(command = "help", description = "command-help-description", aliases = {"?", "hilfe", "ls", "commands"})
public class HelpCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        Driver.getInstance().getTerminalDriver().getCommandDriver().getCommands().forEach(commandAdapter -> {
            StringBuilder aliases;

            if (commandAdapter.getAliases().size() == 1) {
                aliases = new StringBuilder(commandAdapter.getAliases().get(0));
            } else {
                aliases = new StringBuilder(commandAdapter.getAliases().get(0));
                for (int i = 1; i != commandAdapter.getAliases().size(); i++) {
                    aliases.append(", ").append(commandAdapter.getAliases().get(i));
                }
            }

            Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                    " >> §f" + commandAdapter.getCommand() + "  §7'§f" + aliases + "§7' ~ " + Driver.getInstance().getLanguageDriver().getLanguage().getMessage(commandAdapter.getDescription())
            );
        });
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        return null;
    }
}
