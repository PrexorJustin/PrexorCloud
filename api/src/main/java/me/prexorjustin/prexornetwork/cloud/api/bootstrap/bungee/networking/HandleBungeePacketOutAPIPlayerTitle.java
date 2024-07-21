package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerTitle;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandleBungeePacketOutAPIPlayerTitle implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerTitle castedPacket) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(castedPacket.getUsername());
            if (player.isConnected()) {
                Title title = ProxyServer.getInstance().createTitle();
                title.title(TextComponent.fromLegacy(castedPacket.getTitle()));
                title.subTitle(TextComponent.fromLegacy(castedPacket.getSubTitle()));
                title.fadeIn(castedPacket.getFadeIn());
                title.stay(castedPacket.getStay());
                title.fadeOut(castedPacket.getFadeOut());

                player.sendTitle(title);
            }
        }
    }
}
