package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInCommandMaxPlayers extends Packet {

    private String group;
    private Integer amount;

    public PacketInCommandMaxPlayers(String group, Integer amount) {
        setPacketUUID(918439129);
        this.group = group;
        this.amount = amount;
    }

    public PacketInCommandMaxPlayers() {
        setPacketUUID(918439129);
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
