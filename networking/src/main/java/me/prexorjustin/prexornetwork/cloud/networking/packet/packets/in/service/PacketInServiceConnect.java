package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInServiceConnect extends Packet {

    private String service;

    public PacketInServiceConnect() {
        setPacketUUID(239945);
    }

    public PacketInServiceConnect(String service) {
        setPacketUUID(239945);
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.service);
    }
}
