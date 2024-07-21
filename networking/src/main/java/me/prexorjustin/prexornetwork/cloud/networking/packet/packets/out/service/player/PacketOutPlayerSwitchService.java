package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutPlayerSwitchService extends Packet {

    private String name, server, from;

    public PacketOutPlayerSwitchService() {
        setPacketUUID(6332);
    }

    public PacketOutPlayerSwitchService(String name, String server, String from) {
        setPacketUUID(6332);
        this.name = name;
        this.server = server;
        this.from = from;
    }

    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.server = buffer.readString();
        this.from = buffer.readString();
    }

    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.server);
        buffer.writeString(this.from);
    }
}
