package me.prexorjustin.prexornetwork.cloud.api.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPICreateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPIDeleteEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPIReloadEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPIUpdateEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPICreateEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIDeleteEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIReloadEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIUpdateEvent;

public class HandlePacketOutRestAPI implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutCloudRestAPICreateEvent castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudRestAPICreateEvent(castedPacket.getPath(), castedPacket.getContent()));
        } else if (packet instanceof PacketOutCloudRestAPIDeleteEvent castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudRestAPIDeleteEvent(castedPacket.getPath()));
        } else if (packet instanceof PacketOutCloudRestAPIReloadEvent) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudRestAPIReloadEvent());
        } else if (packet instanceof PacketOutCloudRestAPIUpdateEvent castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudRestAPIUpdateEvent(castedPacket.getPath(), castedPacket.getContent()));
        }
    }
}
