package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutGroupCreate extends Packet {

    private String group;

    public PacketOutGroupCreate() {
        setPacketUUID(82342732);
    }

    public PacketOutGroupCreate(String group) {
        setPacketUUID(82342732);
        this.group = group;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        group = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(group);
    }
}
