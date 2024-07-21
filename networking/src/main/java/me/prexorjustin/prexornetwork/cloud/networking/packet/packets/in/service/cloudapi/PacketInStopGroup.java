package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInStopGroup extends Packet {

    private String group;

    public PacketInStopGroup() {
        setPacketUUID(821321);
    }

    public PacketInStopGroup(String group) {
        setPacketUUID(821321);
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
