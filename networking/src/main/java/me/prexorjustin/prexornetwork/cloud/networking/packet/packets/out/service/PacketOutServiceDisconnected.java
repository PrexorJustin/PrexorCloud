package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutServiceDisconnected extends Packet {

    private String name;
    private boolean isProxy;

    public PacketOutServiceDisconnected() {
        setPacketUUID(291234);
    }

    public PacketOutServiceDisconnected(String name, boolean isProxy) {
        setPacketUUID(291234);
        this.name = name;
        this.isProxy = isProxy;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        name = buffer.readString();
        isProxy = buffer.readBoolean();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(name);
        buffer.writeBoolean(isProxy);
    }
}
