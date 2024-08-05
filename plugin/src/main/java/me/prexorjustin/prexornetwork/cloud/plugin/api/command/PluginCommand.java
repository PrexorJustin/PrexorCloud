package me.prexorjustin.prexornetwork.cloud.plugin.api.command;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

@Getter
public abstract class PluginCommand {

    private final String name, description;

    public PluginCommand() {
        PluginCommandInfo annotation = getClass().getAnnotation(PluginCommandInfo.class);
        this.name = annotation.command();
        this.description = annotation.description();
    }

    public abstract void performCommand(PluginCommand command, ProxiedPlayer bungeePlayer, Player velocityPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args);

    public abstract List<String> tabComplete(String[] args);

}
