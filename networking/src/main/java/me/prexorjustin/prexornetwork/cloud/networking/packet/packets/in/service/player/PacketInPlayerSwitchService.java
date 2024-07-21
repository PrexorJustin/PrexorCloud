package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInPlayerSwitchService extends Packet {

    private String name, server;

    public PacketInPlayerSwitchService() {
        setPacketUUID(9829191);
    }

    public PacketInPlayerSwitchService(String name, String server) {
        setPacketUUID(9829191);
        this.name = name;
        this.server = server;
    }

    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.server = buffer.readString();
    }

    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.server);
    }
}
