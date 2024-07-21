package me.prexorjustin.prexornetwork.cloud.api.networking;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.player.async.entrys.AsyncCloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.player.sync.entrys.CloudPlayer;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerDisconnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerSwitchEvent;
import me.prexorjustin.prexornetwork.cloud.driver.storage.uuid.UUIDDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerSwitchService;

import java.util.UUID;

public class HandlePacketOutPlayer implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutPlayerConnect castedPacket) {
            if (CloudAPI.getInstance().getPlayerPool().isPlayerNull(castedPacket.getName())) {
                UUID uuid = UUIDDriver.getUUID(castedPacket.getName());

                CloudAPI.getInstance().getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(castedPacket.getName(), uuid));
                CloudAPI.getInstance().getPlayerPool().registerPlayer(new CloudPlayer(castedPacket.getName(), uuid));

                CloudAPI.getInstance().getEventDriver().executeEvent(
                        new CloudPlayerConnectedEvent(castedPacket.getName(), castedPacket.getProxy(), uuid)
                );
            }
        } else if (packet instanceof PacketOutPlayerDisconnect castedPacket) {
            if (!CloudAPI.getInstance().getPlayerPool().isPlayerNull(castedPacket.getName())) {
                CloudAPI.getInstance().getAsyncPlayerPool().unregisterPlayer(castedPacket.getName());
                CloudAPI.getInstance().getPlayerPool().unregisterPlayer(castedPacket.getName());

                CloudAPI.getInstance().getEventDriver().executeEvent(
                        new CloudPlayerDisconnectedEvent(castedPacket.getName(), UUIDDriver.getUUID(castedPacket.getName()))
                );
            }
        } else if (packet instanceof PacketOutPlayerSwitchService castedPacket) {
            UUID uuid = UUIDDriver.getUUID(castedPacket.getName());

            if (CloudAPI.getInstance().getPlayerPool().isPlayerNull(castedPacket.getName())) {

                CloudAPI.getInstance().getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(castedPacket.getName(), uuid));
                CloudAPI.getInstance().getPlayerPool().registerPlayer(new CloudPlayer(castedPacket.getName(), uuid));

                CloudAPI.getInstance().getEventDriver().executeEvent(
                        new CloudPlayerConnectedEvent(
                                castedPacket.getName(),
                                CloudAPI.getInstance().getPlayerPool().getPlayer(uuid).getProxyServer().getName(),
                                uuid
                        ));
            }

            CloudAPI.getInstance().getEventDriver().executeEvent(
                    new CloudPlayerSwitchEvent(castedPacket.getName(), castedPacket.getFrom(), castedPacket.getServer(), uuid)
            );
        }
    }
}
