package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.networking;

import com.velocitypowered.api.proxy.Player;
import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.VelocityBootstrap;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerKick;
import net.kyori.adventure.text.Component;

public class HandleVelocityPacketOutAPIPlayerKick implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerKick packetCast) {
            Player player = VelocityBootstrap.getInstance().getProxyServer().getPlayer(packetCast.getUsername()).orElse(null);
            assert player != null;

            if (player.isActive()) player.disconnect(Component.text(packetCast.getMessage()));
        }
    }
}
