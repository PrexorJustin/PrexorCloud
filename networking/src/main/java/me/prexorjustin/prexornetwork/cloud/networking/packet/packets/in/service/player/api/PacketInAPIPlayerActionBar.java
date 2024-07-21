package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAPIPlayerActionBar extends Packet {

    private String username, message;

    public PacketInAPIPlayerActionBar() {
        setPacketUUID(94398439);
    }

    public PacketInAPIPlayerActionBar(String username, String message) {
        setPacketUUID(94398439);
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
