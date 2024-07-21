package me.prexorjustin.prexornetwork.cloud.api.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupCreateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupDeleteEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupUpdateEditEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupCreate;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupDelete;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupEdit;

public class HandlePacketOutGroup implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutGroupCreate castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudGroupCreateEvent(castedPacket.getGroup()));
        } else if (packet instanceof PacketOutGroupDelete castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudGroupDeleteEvent(castedPacket.getGroup()));
        } else if (packet instanceof PacketOutGroupEdit castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudGroupUpdateEditEvent(castedPacket.getGroup()));
        }
    }
}
