package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInShutdownNode extends Packet {

    private String node;

    public PacketInShutdownNode() {
        setPacketUUID(87435923);
    }

    public PacketInShutdownNode(String node) {
        setPacketUUID(87435923);
        this.node = node;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.node = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.node);
    }
}
