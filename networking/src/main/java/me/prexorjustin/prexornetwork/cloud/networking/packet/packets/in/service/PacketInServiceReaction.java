package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInServiceReaction extends Packet {

    private String service;

    public PacketInServiceReaction() {
        setPacketUUID(2209492);
    }

    public PacketInServiceReaction(String service) {
        setPacketUUID(2209492);
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(service);
    }
}
