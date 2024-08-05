package me.prexorjustin.prexornetwork.cloud.plugin.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.plugin.api.translate.Translator;
import me.prexorjustin.prexornetwork.cloud.plugin.velocity.VelocityBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
public class VelocityCloudConnectListener {

    private final List<UUID> connected = new ArrayList<>();

    @Subscribe(order = PostOrder.FIRST)
    public void onServerPreConnect(ServerPreConnectEvent event) {
        ServerInfo serverInfo;
        if (event.getOriginalServer().getServerInfo().getName().equalsIgnoreCase("lobby")) {
            serverInfo = Objects.requireNonNull(VelocityBootstrap.getInstance().getProxyServer().getServer(VelocityBootstrap.getInstance().getLobby(event.getPlayer()).getName()).orElse(null)).getServerInfo();
            if (serverInfo != null) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(Objects.requireNonNull(VelocityBootstrap.getInstance().getProxyServer().getServer(serverInfo.getName()).orElse(null))));
            } else event.setResult(ServerPreConnectEvent.ServerResult.denied());
        } else if (event.getOriginalServer() == null) {
            serverInfo = Objects.requireNonNull(VelocityBootstrap.getInstance().getProxyServer().getServer(VelocityBootstrap.getInstance().getLobby(event.getPlayer()).getName()).orElse(null)).getServerInfo();
            if (serverInfo != null) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(Objects.requireNonNull(VelocityBootstrap.getInstance().getProxyServer().getServer(serverInfo.getName()).orElse(null))));
            } else event.setResult(ServerPreConnectEvent.ServerResult.denied());
        }
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        LiveService service = (LiveService) (new ConfigDriver("./CLOUDSERVICE.json")).read(LiveService.class);
        Group group = CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup());

        if (CloudAPI.getInstance().getPlayerPool().getConnectedPlayers().stream().anyMatch(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(event.getPlayer().getUsername())))
            event.getPlayer().disconnect(VelocityBootstrap.getInstance().getMiniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickAlreadyOnNetwork"))));
        else {
            this.connected.add(event.getPlayer().getUniqueId());
            CloudAPI.getInstance().sendPacketSynchronized(new PacketInPlayerConnect(event.getPlayer().getUsername(), service.getName()));

            if (group.isMaintenance()) {
                if (!event.getPlayer().hasPermission("prexorcloud.bypass.connection.maintenance") && !CloudAPI.getInstance().getWhitelist().contains(event.getPlayer().getUsername()))
                    event.getPlayer().disconnect(VelocityBootstrap.getInstance().getMiniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNetworkIsMaintenance"))));
            }
            if (CloudAPI.getInstance().getPlayerPool().getConnectedPlayers().size() >= group.getMaxPlayer()
                    && !event.getPlayer().hasPermission("prexorcloud.bypass.connection.full")
                    && !CloudAPI.getInstance().getWhitelist().contains(event.getPlayer().getUsername())) {
                event.getPlayer().disconnect(VelocityBootstrap.getInstance().getMiniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNetworkIsFull"))));
            } else if (event.getPlayer().isActive() && VelocityBootstrap.getInstance().getLobby(event.getPlayer()) == null)
                event.getPlayer().disconnect(VelocityBootstrap.getInstance().getMiniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNoFallback"))));
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        if (this.connected.contains(event.getPlayer().getUniqueId())) {
            CloudAPI.getInstance().sendPacketSynchronized(new PacketInPlayerDisconnect(event.getPlayer().getUsername()));
        }
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInPlayerSwitchService(event.getPlayer().getUsername(), event.getServer().getServerInfo().getName()));
    }

    @Subscribe
    public void handle(KickedFromServerEvent event) {
        Player player = event.getPlayer();
        if (player.isActive()) {
            CloudService target = VelocityBootstrap.getInstance().getLobby(player, event.getServer().getServerInfo().getName());
            if (target != null)
                event.setResult(KickedFromServerEvent.RedirectPlayer.create(Objects.requireNonNull(VelocityBootstrap.getInstance().getProxyServer().getServer(target.getName()).orElse(null))));
            else
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(VelocityBootstrap.getInstance().getMiniMessage().deserialize(new Translator().translate(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNoFallback")))));
        }
    }
}
