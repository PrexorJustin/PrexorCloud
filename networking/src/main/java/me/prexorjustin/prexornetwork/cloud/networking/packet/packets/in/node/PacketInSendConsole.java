package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketInSendConsole extends Packet {

    private String service, line;

    public PacketInSendConsole() {
        setPacketUUID(230103);
    }

    public PacketInSendConsole(String service, String line) {
        setPacketUUID(230103);
        this.service = service;
        this.line = line;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.service = buffer.readString();
        this.line = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.service);
        buffer.writeString(this.line);
    }
}
