package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketInAPIPlayerConnect extends Packet {

    private String username, service;

    public PacketInAPIPlayerConnect() {
        setPacketUUID(10220230);
    }

    public PacketInAPIPlayerConnect(String username, String service) {
        setPacketUUID(10220230);
        this.username = username;
        this.service = service;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.username = buffer.readString();
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.service);
    }
}
