package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutGroupDelete extends Packet {

    private String group;

    public PacketOutGroupDelete() {
        setPacketUUID(9849929);
    }

    public PacketOutGroupDelete(String group) {
        setPacketUUID(9849929);
        this.group = group;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.group = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.group);
    }

}
