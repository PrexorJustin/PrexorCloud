package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.implementation;

import com.velocitypowered.api.proxy.Player;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommandInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

@PluginCommandInfo(command = "info", description = "/service info")
public class InfoCommand extends PluginCommand {

    @Override
    public void performCommand(PluginCommand command, ProxiedPlayer bungeePlayer, Player velocityPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args) {
        if (bukkitPlayer != null && bungeePlayer == null && velocityPlayer == null) {
            String prefix = CloudAPI.getInstance().getMessageConfig().getMessages().get("prefix");
            CloudService service = CloudAPI.getInstance().getServicePool().getService(CloudAPI.getInstance().getService().getName());
            String maintenance = service.getGroup().isMaintenance() ? "§cMaintenance" : "§aMaintenance";

            bukkitPlayer.sendMessage(
                    prefix + "Name: §f" + service.getName(),
                    prefix + "Group: §f" + service.getGroupName() + " §r(" + maintenance + "§r)",
                    prefix + "State: §f" + service.getState(),
                    prefix + "Host: §f" + service.getAddress() + "§r:§f" + service.getPort(),
                    prefix + "Players: §f" + service.getPlayerCount()

            );
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return List.of();
    }
}
