package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInNodeActionSuccess extends Packet {

    private boolean launched;
    private String service, node;
    private int port;

    public PacketInNodeActionSuccess() {
        setPacketUUID(392921);
    }

    public PacketInNodeActionSuccess(boolean launched, String service, String node, int port) {
        setPacketUUID(392921);
        this.launched = launched;
        this.service = service;
        this.node = node;
        this.port = port;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.launched = buffer.readBoolean();
        this.service = buffer.readString();
        this.node = buffer.readString();
        this.port = buffer.readInt();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeBoolean(this.launched);
        buffer.writeString(this.service);
        buffer.writeString(this.node);
        buffer.writeInt(this.port);
    }
}
