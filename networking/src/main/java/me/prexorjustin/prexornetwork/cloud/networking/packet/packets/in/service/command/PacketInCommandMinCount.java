package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInCommandMinCount extends Packet {

    private String group;
    private Integer amount;

    public PacketInCommandMinCount(String group, Integer amount) {
        setPacketUUID(4953982);
        this.group = group;
        this.amount = amount;
    }

    public PacketInCommandMinCount() {
        setPacketUUID(4953982);
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.group = buffer.readString();
        this.amount = buffer.readInt();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.group);
        buffer.writeInt(this.amount);
    }
}
