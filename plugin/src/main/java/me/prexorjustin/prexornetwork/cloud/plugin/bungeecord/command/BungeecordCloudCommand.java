package me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.command;

import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.plugin.api.PluginDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.api.command.PluginCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;

public class BungeecordCloudCommand extends Command implements TabExecutor {

    public BungeecordCloudCommand() {
        super("cloud", "", "prexorcloud", "pc");
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer player) {
            if (player.hasPermission("prexorcloud.command.use") || player.hasPermission("prexorcloud.command.*")) {
                if (args.length == 0) sendHelp(player);
                else {
                    if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                        String[] refreshedArgs = Arrays.copyOfRange(args, 1, args.length);
                        PluginDriver.getInstance().getCommand(args[0]).performCommand(
                                PluginDriver.getInstance().getCommand(args[0]), player, null, null, refreshedArgs
                        );
                    } else sendHelp(player);
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> suggestions = new ArrayList<>();

        if (args.length == 1)
            suggestions.addAll(PluginDriver.getInstance().getCommands().stream().map(PluginCommand::getName).toList());
        else {
            if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                if (args.length == 2)
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(new String[]{}));
                else {
                    String[] refreshedArgs = Arrays.copyOfRange(args, 1, args.length);
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(refreshedArgs));
                }
            }
        }

        String prefix = args[args.length - 1].toLowerCase();

        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(prefix)).toList();
    }

    private void sendHelp(ProxiedPlayer player) {
        String prefix = CloudAPI.getInstance().getMessageConfig().getMessages().get("prefix");
        PluginDriver.getInstance().getCommands().forEach(command -> player.sendMessage(TextComponent.fromLegacyText(prefix + command.getDescription())));
    }
}
