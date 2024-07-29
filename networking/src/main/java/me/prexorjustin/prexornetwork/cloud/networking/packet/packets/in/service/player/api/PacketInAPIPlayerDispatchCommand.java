package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAPIPlayerDispatchCommand extends Packet {

    private String username, command;

    public PacketInAPIPlayerDispatchCommand() {
        setPacketUUID(984322181);
    }

    public PacketInAPIPlayerDispatchCommand(String userName, String command) {
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
