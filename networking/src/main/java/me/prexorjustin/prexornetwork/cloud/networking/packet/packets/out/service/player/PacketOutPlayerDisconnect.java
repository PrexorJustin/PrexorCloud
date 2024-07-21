package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutPlayerDisconnect extends Packet {

    private String name;

    public PacketOutPlayerDisconnect() {
        setPacketUUID(8643);
    }

    public PacketOutPlayerDisconnect(String name) {
        setPacketUUID(8643);
        this.name = name;
    }

    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
    }

    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
    }
}
