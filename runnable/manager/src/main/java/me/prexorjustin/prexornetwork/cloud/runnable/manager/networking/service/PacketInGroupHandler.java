package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInCreateGroup;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDeleteGroup;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInStopGroup;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

public class PacketInGroupHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInCreateGroup packetCast) {
            Driver.getInstance().getGroupDriver().create((Group) new ConfigDriver().convert(packetCast.getGroupConfig(), Group.class));
        } else if (packet instanceof PacketInDeleteGroup packetCast) {
            Driver.getInstance().getGroupDriver().delete(packetCast.getGroup());
            PrexorCloudManager.serviceDriver.delete.add(packetCast.getGroup());
            PrexorCloudManager.serviceDriver.getServices(packetCast.getGroup()).forEach(taskedService -> PrexorCloudManager.serviceDriver.unregister(taskedService.getEntry().getServiceName()));
        } else if (packet instanceof PacketInStopGroup packetCast) {
            PrexorCloudManager.serviceDriver.getServices(packetCast.getGroup()).forEach(taskedService -> PrexorCloudManager.serviceDriver.unregister(taskedService.getEntry().getServiceName()));
        }
    }
}
