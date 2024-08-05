package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        CloudAPI.getInstance().getCloudService().shutdown();
        return false;
    }
}
