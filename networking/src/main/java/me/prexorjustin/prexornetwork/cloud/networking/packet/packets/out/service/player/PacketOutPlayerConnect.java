package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutPlayerConnect extends Packet {

    private String name, proxy;

    public PacketOutPlayerConnect() {
        setPacketUUID(423293);
    }

    public PacketOutPlayerConnect(String name, String proxy) {
        setPacketUUID(423293);
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
