package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInCommandWhitelist extends Packet {

    private String name;

    public PacketInCommandWhitelist() {
        setPacketUUID(890349800);
    }

    public PacketInCommandWhitelist(String name) {
        setPacketUUID(890349800);
        this.name = name;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
    }
}
