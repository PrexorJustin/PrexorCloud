package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.networking;

import com.velocitypowered.api.proxy.Player;
import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.VelocityBootstrap;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerTitle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public class HandleVelocityPacketOutAPIPlayerTitle implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerTitle packetCast) {
            Player player = VelocityBootstrap.getInstance().getProxyServer().getPlayer(packetCast.getUsername()).orElse(null);
            assert player != null;

            if (player.isActive()) {
                Title title = Title.title(
                        Component.text(packetCast.getTitle()),
                        Component.text(packetCast.getSubTitle()),
                        Title.Times.times(
                                Duration.ofSeconds(Long.getLong(String.valueOf(packetCast.getFadeIn()))),
                                Duration.ofSeconds(Long.getLong(String.valueOf(packetCast.getStay()))),
                                Duration.ofSeconds(Long.getLong(String.valueOf(packetCast.getFadeOut())))
                        )
                );

                player.showTitle(title);
            }
        }
    }
}
