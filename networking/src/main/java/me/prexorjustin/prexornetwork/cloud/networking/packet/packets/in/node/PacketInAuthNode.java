package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAuthNode extends Packet {

    private String node, key;

    public PacketInAuthNode() {
        setPacketUUID(298234);
    }

    public PacketInAuthNode(String node, String key) {
        setPacketUUID(298234);
        this.node = node;
        this.key = key;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.node = buffer.readString();
        this.key = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.node);
        buffer.writeString(this.key);
    }
}
