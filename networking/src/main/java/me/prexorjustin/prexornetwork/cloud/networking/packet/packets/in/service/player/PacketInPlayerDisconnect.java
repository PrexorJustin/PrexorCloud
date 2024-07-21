package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInPlayerDisconnect extends Packet {

    private String name;

    public PacketInPlayerDisconnect() {
        setPacketUUID(192351);
    }

    public PacketInPlayerDisconnect(String name) {
        setPacketUUID(192351);
        this.name = name;
    }

    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
    }

    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
    }

}
