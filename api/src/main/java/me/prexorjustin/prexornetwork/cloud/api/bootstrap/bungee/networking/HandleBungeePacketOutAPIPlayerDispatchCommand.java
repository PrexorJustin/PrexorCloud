package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerDispatchCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandleBungeePacketOutAPIPlayerDispatchCommand implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerDispatchCommand packetOutAPIPlayerDispatchCommand) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetOutAPIPlayerDispatchCommand.getUsername());
            if (player.isConnected())
                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, packetOutAPIPlayerDispatchCommand.getCommand());
        }
    }
}
