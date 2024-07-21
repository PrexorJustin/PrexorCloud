package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInPlayerConnect extends Packet {

    private String name, proxy;

    public PacketInPlayerConnect() {
        setPacketUUID(2995985);
    }

    public PacketInPlayerConnect(String name, String proxy) {
        setPacketUUID(2995985);
        this.name = name;
        this.proxy = proxy;
    }

    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.proxy = buffer.readString();
    }

    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.proxy);
    }
}
