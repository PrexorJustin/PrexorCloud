package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.networking;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.VelocityBootstrap;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerConnect;

public class HandleVelocityPacketOutAPIPlayerConnect implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerConnect packetCast) {
            Player player = VelocityBootstrap.getInstance().getProxyServer().getPlayer(packetCast.getUsername()).orElse(null);
            RegisteredServer server = VelocityBootstrap.getInstance().getProxyServer().getServer(packetCast.getService()).orElse(null);

            assert player != null && server != null;

            if (player.isActive()) player.createConnectionRequest(server);
        }
    }
}
