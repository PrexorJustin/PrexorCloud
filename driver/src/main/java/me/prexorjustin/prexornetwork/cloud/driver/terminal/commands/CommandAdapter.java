package me.prexorjustin.prexornetwork.cloud.driver.terminal.commands;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class CommandAdapter {

    private final String command, description, permission;
    private final String[] aliases;

    public CommandAdapter() {
        CommandInfo annotation = getClass().getAnnotation(CommandInfo.class);

        this.command = annotation.command();
        this.description = annotation.description();
        this.permission = annotation.permission();
        this.aliases = annotation.aliases();
    }

    public abstract void performCommand(CommandAdapter command, String[] args);

    public abstract ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args);

    public List<String> getAliases() {
        return Arrays.stream(this.aliases).toList();
    }
}
