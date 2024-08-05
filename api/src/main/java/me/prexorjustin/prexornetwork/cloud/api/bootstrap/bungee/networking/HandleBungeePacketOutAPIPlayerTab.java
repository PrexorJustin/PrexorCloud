package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerTab;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandleBungeePacketOutAPIPlayerTab implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerTab castedPacket) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(castedPacket.getUsername());
            if (player.isConnected())
                player.setTabHeader(
                        TextComponent.fromLegacyText(castedPacket.getHeader()),
                        TextComponent.fromLegacyText(castedPacket.getFooter())
                );
        }
    }
}
