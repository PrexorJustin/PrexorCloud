package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.*;

public class PacketInAPIHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInAPIPlayerActionBar packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutAPIPlayerActionBar(packetCast.getUsername(), packetCast.getMessage()));
        } else if (packet instanceof PacketInAPIPlayerTab packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutAPIPlayerTab(packetCast.getUsername(), packetCast.getHeader(), packetCast.getFooter()));
        } else if (packet instanceof PacketInAPIPlayerTitle packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutAPIPlayerTitle(
                    packetCast.getTitle(),
                    packetCast.getSubTitle(),
                    packetCast.getFadeIn(),
                    packetCast.getStay(),
                    packetCast.getFadeOut(),
                    packetCast.getUsername()
            ));
        } else if (packet instanceof PacketInAPIPlayerConnect packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutAPIPlayerConnect(packetCast.getUsername(), packetCast.getService()));
        } else if (packet instanceof PacketInAPIPlayerKick packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutAPIPlayerKick(packetCast.getUsername(), packetCast.getMessage()));
        } else if (packet instanceof PacketInAPIPlayerMessage packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutAPIPlayerMessage(packetCast.getUsername(), packetCast.getMessage()));
        }
    }

}
