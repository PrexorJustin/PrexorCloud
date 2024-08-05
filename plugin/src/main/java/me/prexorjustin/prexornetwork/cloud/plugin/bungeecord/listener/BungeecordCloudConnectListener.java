package me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.listener;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.plugin.api.translate.Translator;
import me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.BungeecordBootstrap;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BungeecordCloudConnectListener implements Listener {

    private final List<UUID> connected = new ArrayList<>();

    private ServerInfo serverInfo;

    @EventHandler(priority = -127)
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (this.connected.contains(player.getUniqueId())) {
            this.serverInfo = ProxyServer.getInstance().getServerInfo(BungeecordBootstrap.getInstance().getLobby(player).getName());
            if (this.serverInfo != null) event.setTarget(this.serverInfo);
            else event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(PostLoginEvent event) {
        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        Group group = CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup());
        ProxiedPlayer player = event.getPlayer();
        Messages messageConfig = CloudAPI.getInstance().getMessageConfig();


        if (CloudAPI.getInstance().getPlayerPool().getConnectedPlayers().stream().anyMatch(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(player.getName())))
            player.disconnect(TextComponent.fromLegacyText(messageConfig.getMessages().get("kickAlreadyOnNetwork").replace("&", "§")));
        else {
            this.connected.add(player.getUniqueId());
            CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerConnect(player.getName(), service.getName()));

            if (group.isMaintenance())
                if (ProxyServer.getInstance().getPlayer(player.getUniqueId()) != null
                        && !player.hasPermission("prexorcloud.maintenance.bypass")
                        && !CloudAPI.getInstance().getWhitelist().contains(player.getName()))
                    player.disconnect(BungeeComponentSerializer.get().serialize(
                            MiniMessage.miniMessage().deserialize(new Translator().translate(messageConfig.getMessages().get("kickNetworkIsMaintenance")))
                    )[0]);

            if (CloudAPI.getInstance().getPlayerPool().getConnectedPlayers().size() >= group.getMaxPlayer()
                    && !player.hasPermission("prexorcloud.bypass.connection.full")
                    && !CloudAPI.getInstance().getWhitelist().contains(player.getName())) {
                player.disconnect(BungeeComponentSerializer.get().serialize(
                        MiniMessage.miniMessage().deserialize(new Translator().translate(messageConfig.getMessages().get("kickNetworkIsFull")))
                )[0]);
            } else if (ProxyServer.getInstance().getPlayer(player.getUniqueId()) != null
                    && BungeecordBootstrap.getInstance().getLobby(player) == null) {
                player.disconnect(BungeeComponentSerializer.get().serialize(
                        MiniMessage.miniMessage().deserialize(new Translator().translate(messageConfig.getMessages().get("kickNoFallback")))
                )[0]);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (this.connected.contains(player.getUniqueId())) {
            this.connected.remove(player.getUniqueId());
            CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerDisconnect(player.getName()));
        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (this.connected.contains(player.getUniqueId()))
            CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerSwitchService(player.getName(), player.getServer().getInfo().getName()));
    }

    @EventHandler(priority = -127)
    public void onServerKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (this.connected.contains(player.getUniqueId())) {
            CloudService service = BungeecordBootstrap.getInstance().getLobby(player, event.getKickedFrom().getName());

            if (service == null) {
                event.setCancelled(false);
                event.setCancelServer(null);
                event.getPlayer().disconnect(
                        BungeeComponentSerializer.get().serialize(
                                MiniMessage.miniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNoFallback")))
                        )[0]
                );
            } else {
                this.serverInfo = ProxyServer.getInstance().getServerInfo(service.getName());
                if (this.serverInfo != null) {
                    event.setCancelServer(this.serverInfo);
                    event.setCancelled(true);
                } else {
                    event.setCancelled(false);
                    event.setCancelServer(null);
                    event.getPlayer().disconnect(
                            BungeeComponentSerializer.get().serialize(
                                    MiniMessage.miniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNoFallback")))
                            )[0]
                    );
                }
            }
        }
    }
}
