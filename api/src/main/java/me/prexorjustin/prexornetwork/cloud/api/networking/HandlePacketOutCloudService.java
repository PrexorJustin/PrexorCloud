package me.prexorjustin.prexornetwork.cloud.api.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyChangeStateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceChangeStateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutCloudProxyChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutCloudServiceChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudProxyCouldNotStartEvent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudServiceCouldNotStartEvent;

public class HandlePacketOutCloudService implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutCloudProxyChangeState castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyChangeStateEvent(castedPacket.getName(), castedPacket.getState()));
        } else if (packet instanceof PacketOutCloudProxyCouldNotStartEvent castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyCouldNotStartEvent(castedPacket.getName()));
        } else if (packet instanceof PacketOutCloudServiceChangeState castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServiceChangeStateEvent(castedPacket.getName(), castedPacket.getState()));
        } else if (packet instanceof PacketOutCloudServiceCouldNotStartEvent castedPacket) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServiceCouldNotStartEvent(castedPacket.getName()));
        }
    }
}
