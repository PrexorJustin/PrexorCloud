package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutGroupEdit extends Packet {

    private String group;

    public PacketOutGroupEdit() {
        setPacketUUID(43212312);
    }

    public PacketOutGroupEdit(String group) {
        setPacketUUID(43212312);
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
