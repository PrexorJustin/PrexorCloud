package me.prexorjustin.prexornetwork.cloud.plugin.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommand;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PluginDriver {

    @Getter
    private static PluginDriver instance;

    private final List<PluginCommand> commands;

    public PluginDriver() {
        instance = this;
        this.commands = new ArrayList<>();
    }

    public void registerCommand(PluginCommand command) {
        this.commands.add(command);
    }

    public PluginCommand getCommand(String name) {
        return this.commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
