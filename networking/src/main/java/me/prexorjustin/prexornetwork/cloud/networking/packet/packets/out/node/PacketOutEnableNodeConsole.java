package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

public class PacketOutEnableNodeConsole extends Packet {

    public PacketOutEnableNodeConsole() {
        setPacketUUID(191230131);
    }

    @Override
    public void readPacket(NettyBuffer buffer) {

    }

    @Override
    public void writePacket(NettyBuffer buffer) {

    }
}
