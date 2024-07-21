package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandleBungeePacketOutAPIPlayerActionBar implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerActionBar packetOutAPIPlayerActionBar) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetOutAPIPlayerActionBar.getUsername());
            if (player.isConnected()) {
                player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(packetOutAPIPlayerActionBar.getMessage()));
            }
        }
    }
}
