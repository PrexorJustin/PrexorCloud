package me.prexorjustin.prexornetwork.cloud.driver.terminal.commands;

import lombok.Getter;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandDriver {

    private final List<CommandAdapter> commands;

    public CommandDriver() {
        this.commands = new ArrayList<>();
    }

    @SneakyThrows
    public void executeCommand(String line) {
        CommandAdapter command = getCommand(line.split(" ")[0]);
        String[] args = Driver.getInstance().getMessageStorage().dropFirstString(line.split(" "));
        if (command != null) command.performCommand(command, args);
        else
            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-not-found"));
    }

    public CommandAdapter getCommand(String name) {
        for (CommandAdapter command : this.commands) {
            if (command.getCommand().equalsIgnoreCase(name)) return command;
            if (command.getAliases().contains(name)) return command;
        }

        return null;
    }

    public void registerCommand(CommandAdapter command) {
        this.commands.add(command);
    }
}
