package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketOutCloudRestAPIDeleteEvent extends Packet {

    private String path;

    public PacketOutCloudRestAPIDeleteEvent() {
        setPacketUUID(323233122);
    }

    public PacketOutCloudRestAPIDeleteEvent(String path) {
        setPacketUUID(323233122);
        this.path = path;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {

    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {

    }
}
