package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutAPIPlayerKick extends Packet {

    private String username, message;

    public PacketOutAPIPlayerKick() {
        setPacketUUID(829341212);
    }

    public PacketOutAPIPlayerKick(String username, String message) {
        setPacketUUID(829341212);
        this.username = username;
        this.message = message;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.username = buffer.readString();
        this.message = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.message);
    }
}
