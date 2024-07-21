package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInSendConsoleFromNode extends Packet {

    private String line;

    public PacketInSendConsoleFromNode() {
        setPacketUUID(29123812);
    }

    public PacketInSendConsoleFromNode(String line) {
        setPacketUUID(29123812);
        this.line = line;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.line = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.line);
    }
}
