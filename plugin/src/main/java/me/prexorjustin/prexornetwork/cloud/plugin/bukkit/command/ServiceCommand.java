package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.plugin.api.PluginDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (player.hasPermission("prexorcloud.command.service") || player.hasPermission("prexorcloud.command.*")) {
                if (args.length == 0) sendHelp(player);
                else {
                    if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                        PluginCommand pluginCommand = PluginDriver.getInstance().getCommand(args[0]);
                        @NotNull String[] pluginCommandArgs = Arrays.copyOfRange(args, 1, args.length);

                        pluginCommand.performCommand(pluginCommand, null, null, player, pluginCommandArgs);
                    } else sendHelp(player);
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ArrayList<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            PluginDriver.getInstance().getCommands().forEach(command1 -> suggestions.add(command1.getName()));
        } else {
            if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                if (args.length == 2)
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(new String[]{}));
                else {
                    @NotNull String[] refreshedArgs = Arrays.copyOfRange(args, 1, args.length);
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(refreshedArgs));
                }
            }
        }
        return suggestions;
    }

    private void sendHelp(Player player) {
        PluginDriver.getInstance().getCommands().forEach(command -> player.sendMessage(CloudAPI.getInstance().getMessageConfig().getMessages().get("prefix") + command.getDescription()));
    }
}
