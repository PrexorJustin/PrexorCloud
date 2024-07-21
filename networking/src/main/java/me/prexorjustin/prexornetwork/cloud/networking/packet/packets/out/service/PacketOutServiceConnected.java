package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutServiceConnected extends Packet {

    private String name, group;

    public PacketOutServiceConnected() {
        setPacketUUID(288123);
    }

    public PacketOutServiceConnected(String name, String group) {
        setPacketUUID(288123);
        this.name = name;
        this.group = group;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.group = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.group);
    }
}
