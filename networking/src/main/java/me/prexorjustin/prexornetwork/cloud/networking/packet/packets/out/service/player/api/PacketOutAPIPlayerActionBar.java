package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutAPIPlayerActionBar extends Packet {

    private String username, message;

    public PacketOutAPIPlayerActionBar() {
        setPacketUUID(4838291);
    }

    public PacketOutAPIPlayerActionBar(String username, String message) {
        setPacketUUID(4838291);
        this.username = username;
        this.message = message;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        username = buffer.readString();
        message = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.message);
    }
}
