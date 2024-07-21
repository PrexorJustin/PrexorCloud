package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutAPIPlayerDispatchCommand extends Packet {

    private String username, command;

    public PacketOutAPIPlayerDispatchCommand() {
        setPacketUUID(984322181);
    }

    public PacketOutAPIPlayerDispatchCommand(String userName, String command) {
        setPacketUUID(984322181);
        this.username = userName;
        this.command = command;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.username = buffer.readString();
        this.command = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.command);
    }
}
