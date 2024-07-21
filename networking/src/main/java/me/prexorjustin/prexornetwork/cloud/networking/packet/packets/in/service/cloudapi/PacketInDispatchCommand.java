package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketInDispatchCommand extends Packet {

    private String service, command;

    public PacketInDispatchCommand() {
        setPacketUUID(921234);
    }

    public PacketInDispatchCommand(String service, String command) {
        setPacketUUID(921234);
        this.service = service;
        this.command = command;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.service = buffer.readString();
        this.command = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.service);
        buffer.writeString(this.command);
    }
}
