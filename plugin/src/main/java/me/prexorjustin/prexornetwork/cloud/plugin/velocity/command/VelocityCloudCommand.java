package me.prexorjustin.prexornetwork.cloud.plugin.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.plugin.api.PluginDriver;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VelocityCloudCommand implements SimpleCommand {

    @SneakyThrows
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (invocation.source() instanceof Player player) {
            if (player.hasPermission("prexorcloud.command.use")) {
                if (args.length == 0) sendHelp(player);
                else {
                    if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                        String[] refreshedArguments = Arrays.copyOfRange(args, 1, args.length);
                        PluginDriver.getInstance().getCommand(args[0]).performCommand(
                                PluginDriver.getInstance().getCommand(args[0]),
                                null, player, null, refreshedArguments
                        );
                    } else sendHelp(player);
                }
            }
        }
    }

    public void sendHelp(Player player) {
        Messages messages = CloudAPI.getInstance().getMessageConfig();
        String PREFIX = messages.getMessages().get("prefix").replace("&", "§");
        PluginDriver.getInstance().getCommands().forEach(proxyCommand -> player.sendMessage(Component.text(PREFIX + proxyCommand.getDescription())));
    }


    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1)
            PluginDriver.getInstance().getCommands().forEach(proxyCommand -> suggestions.add(proxyCommand.getName()));
        else {
            if (PluginDriver.getInstance().getCommand(args[0]) != null) {
                if (args.length == 2) {
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(new String[]{}));
                } else {
                    String[] refreshedArguments = Arrays.copyOfRange(args, 1, args.length);
                    suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(refreshedArguments));
                }
            }
        }

        String prefix = args[args.length - 1].toLowerCase();
        List<String> filteredSuggestions = suggestions.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(prefix))
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(filteredSuggestions);
    }
}
