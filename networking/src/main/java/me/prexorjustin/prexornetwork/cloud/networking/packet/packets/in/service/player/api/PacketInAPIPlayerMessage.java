package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAPIPlayerMessage extends Packet {

    private String username, message;

    public PacketInAPIPlayerMessage() {
        setPacketUUID(2905493);
    }

    public PacketInAPIPlayerMessage(String username, String message) {
        setPacketUUID(2905493);
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
