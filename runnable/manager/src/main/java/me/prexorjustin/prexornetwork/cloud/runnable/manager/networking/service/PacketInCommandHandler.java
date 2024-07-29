package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchCommand;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchMainCommand;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerDispatchCommand;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

public class PacketInCommandHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInDispatchCommand packetCast) {
            PrexorCloudManager.serviceDriver.getService(packetCast.getService()).handleExecute(packetCast.getCommand());
        } else if (packet instanceof PacketInDispatchMainCommand packetCast) {
            Driver.getInstance().getTerminalDriver().getCommandDriver().executeCommand(packetCast.getCommand());
        } else if (packet instanceof PacketInAPIPlayerDispatchCommand packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketInAPIPlayerDispatchCommand(packetCast.getUsername(), packetCast.getCommand()));
        }
    }

}
