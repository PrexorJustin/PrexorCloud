package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutSendCommand extends Packet {

    private String command, service;

    public PacketOutSendCommand() {
        setPacketUUID(59488922);
    }

    public PacketOutSendCommand(String command, String service) {
        setPacketUUID(59488922);
        this.command = command;
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.command = buffer.readString();
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.command);
        buffer.writeString(this.service);
    }
}
