package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutAPIPlayerConnect extends Packet {

    private String username, service;

    public PacketOutAPIPlayerConnect() {
        setPacketUUID(231231);
    }

    public PacketOutAPIPlayerConnect(String username, String service) {
        setPacketUUID(231231);
        this.username = username;
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.username = buffer.readString();
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.service);
    }
}
