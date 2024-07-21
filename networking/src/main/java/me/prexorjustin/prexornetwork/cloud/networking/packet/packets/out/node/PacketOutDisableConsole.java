package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutDisableConsole extends Packet {

    private String service;

    public PacketOutDisableConsole() {
        setPacketUUID(2323244);
    }

    public PacketOutDisableConsole(String service) {
        setPacketUUID(2323244);
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.service);
    }

}