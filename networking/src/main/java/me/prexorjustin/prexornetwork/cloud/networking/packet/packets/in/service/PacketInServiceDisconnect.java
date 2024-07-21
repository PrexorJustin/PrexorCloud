package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInServiceDisconnect extends Packet {

    private String service;

    public PacketInServiceDisconnect() {
        setPacketUUID(241212);
    }

    public PacketInServiceDisconnect(String service) {
        setPacketUUID(241212);
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
