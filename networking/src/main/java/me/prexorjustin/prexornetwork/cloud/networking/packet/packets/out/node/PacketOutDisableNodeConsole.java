package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

public class PacketOutDisableNodeConsole extends Packet {


    public PacketOutDisableNodeConsole() {
        setPacketUUID(328993212);
    }

    @Override
    public void readPacket(NettyBuffer buffer) {

    }

    @Override
    public void writePacket(NettyBuffer buffer) {

    }
}
