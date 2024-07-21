package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketInDispatchMainCommand extends Packet {

    private String command;

    public PacketInDispatchMainCommand() {
        setPacketUUID(53123);
    }

    public PacketInDispatchMainCommand(String command) {
        setPacketUUID(53123);
        this.command = command;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.command = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.command);
    }
}
