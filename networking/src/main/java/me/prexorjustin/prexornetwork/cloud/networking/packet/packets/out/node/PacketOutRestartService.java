package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutRestartService extends Packet {

    private String service;

    public PacketOutRestartService() {
        setPacketUUID(942900);
    }

    public PacketOutRestartService(String service) {
        setPacketUUID(942900);
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
