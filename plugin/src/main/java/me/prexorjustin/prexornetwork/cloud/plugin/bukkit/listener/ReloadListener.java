package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {

    @EventHandler
    public void onReloadCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("/reload") || message.startsWith("/rl") || message.startsWith("/rl confirm") || message.startsWith("/reload confirm")) {
            event.setCancelled(true);
            if (player.hasPermission("bukkit.command.reload")) {
                player.sendMessage("§cPrexorCloud cannot be reloaded!");
            }
        }
    }
}
