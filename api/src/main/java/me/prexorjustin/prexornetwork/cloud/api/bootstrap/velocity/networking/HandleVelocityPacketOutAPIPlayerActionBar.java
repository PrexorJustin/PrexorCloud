package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.networking;

import com.velocitypowered.api.proxy.Player;
import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.VelocityBootstrap;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerActionBar;
import net.kyori.adventure.text.Component;

public class HandleVelocityPacketOutAPIPlayerActionBar implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerActionBar packetCast) {
            Player player = VelocityBootstrap.getInstance().getProxyServer().getPlayer(packetCast.getUsername()).orElse(null);
            assert player != null;

            if (player.isActive()) player.sendActionBar(Component.text(packetCast.getMessage()));
        }
    }
}