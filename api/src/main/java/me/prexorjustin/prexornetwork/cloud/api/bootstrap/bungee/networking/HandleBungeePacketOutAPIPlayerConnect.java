package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerConnect;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandleBungeePacketOutAPIPlayerConnect implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerConnect packetOutAPIPlayerConnect) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetOutAPIPlayerConnect.getUsername());
            if (player.isConnected())
                player.connect(ProxyServer.getInstance().getServerInfo(packetOutAPIPlayerConnect.getService()));
        }
    }
}
