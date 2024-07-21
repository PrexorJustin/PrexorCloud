package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

public class PacketOutShutdownNode extends Packet {

    public PacketOutShutdownNode() {
        setPacketUUID(90329232);
    }

    @Override
    public void readPacket(NettyBuffer buffer) {

    }

    @Override
    public void writePacket(NettyBuffer buffer) {

    }
}
