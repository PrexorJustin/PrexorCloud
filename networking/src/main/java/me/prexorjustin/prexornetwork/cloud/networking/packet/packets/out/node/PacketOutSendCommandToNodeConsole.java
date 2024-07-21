package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutSendCommandToNodeConsole extends Packet {

    private String command;


    public PacketOutSendCommandToNodeConsole() {
        setPacketUUID(4399293);
    }

    public PacketOutSendCommandToNodeConsole(String command) {
        setPacketUUID(4399293);
        this.command = command;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.command = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.command);
    }
}
