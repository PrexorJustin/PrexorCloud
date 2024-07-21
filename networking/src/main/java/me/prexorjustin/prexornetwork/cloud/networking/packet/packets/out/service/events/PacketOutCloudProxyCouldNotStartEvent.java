package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketOutCloudProxyCouldNotStartEvent extends Packet {

    private String name;

    public PacketOutCloudProxyCouldNotStartEvent() {
        setPacketUUID(879342879);
    }

    public PacketOutCloudProxyCouldNotStartEvent(String name) {
        setPacketUUID(879342879);
        this.name = name;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.name = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.name);
    }
}
