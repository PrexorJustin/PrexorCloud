package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.implementation;

import com.velocitypowered.api.proxy.Player;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommandInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

@PluginCommandInfo(command = "shutdown", description = "/service shutdown")
public class ShutdownCommand extends PluginCommand {

    @Override
    public void performCommand(PluginCommand command, ProxiedPlayer bungeePlayer, Player velocityPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args) {
        CloudAPI.getInstance().getCloudService().shutdown();
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return List.of();
    }
}
