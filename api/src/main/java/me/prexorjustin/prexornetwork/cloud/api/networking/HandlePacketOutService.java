package me.prexorjustin.prexornetwork.cloud.api.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.async.entrys.AsyncCloudService;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceConnected;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceDisconnected;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceLaunch;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServicePrepared;

public class HandlePacketOutService implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutServiceConnected castedPacket) {
            if (!CloudAPI.getInstance().getServicePool().doesServiceExist(castedPacket.getName())) {
                CloudAPI.getInstance().getServicePool().registerService(new CloudService(castedPacket.getName(), castedPacket.getGroup()));
                CloudAPI.getInstance().getAsyncServicePool().registerService(new AsyncCloudService(castedPacket.getName(), castedPacket.getGroup()));

                CloudService service = CloudAPI.getInstance().getServicePool().getService(castedPacket.getName());
                if (service.getGroup().getGroupType().equalsIgnoreCase("PROXY")) {
                    CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyConnectedEvent(
                            service.getName(),
                            service.getGroup().getStorage().getRunningNode(),
                            service.getAddress(),
                            service.getGroupName(),
                            service.getPort()
                    ));
                } else {
                    CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServiceConnectedEvent(
                            service.getName(),
                            service.getGroup().getStorage().getRunningNode(),
                            service.getAddress(),
                            service.getGroupName(),
                            service.getPort()
                    ));
                }
            }
        } else if (packet instanceof PacketOutServiceDisconnected castedPacket) {
            if (castedPacket.isProxy())
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyDisconnectedEvent(castedPacket.getName()));
            else
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServiceDisconnectedEvent(castedPacket.getName()));

            CloudAPI.getInstance().getServicePool().unregisterService(castedPacket.getName());
            CloudAPI.getInstance().getAsyncServicePool().unregisterService(castedPacket.getName());
        } else if (packet instanceof PacketOutServiceLaunch castedPacket) {
            if (castedPacket.isProxy())
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyLaunchEvent(
                        castedPacket.getName(),
                        castedPacket.getGroup(),
                        castedPacket.getNode()
                ));
            else
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServiceLaunchEvent(
                        castedPacket.getName(),
                        castedPacket.getGroup(),
                        castedPacket.getNode()
                ));
        } else if (packet instanceof PacketOutServicePrepared castedPacket) {
            if (castedPacket.isProxy())
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyPreparedEvent(
                        castedPacket.getName(),
                        castedPacket.getGroup(),
                        castedPacket.getNode()
                ));
            else
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServicePreparedEvent(
                        castedPacket.getName(),
                        castedPacket.getGroup(),
                        castedPacket.getNode()
                ));
        }
    }
}
