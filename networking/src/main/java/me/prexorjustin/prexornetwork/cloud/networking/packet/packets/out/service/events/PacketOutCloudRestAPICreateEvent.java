package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketOutCloudRestAPICreateEvent extends Packet {

    private String path, content;

    public PacketOutCloudRestAPICreateEvent(String path, String content) {
        setPacketUUID(904303032);
        this.path = path;
        this.content = content;
    }

    public PacketOutCloudRestAPICreateEvent() {
        setPacketUUID(904303032);
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.path = buffer.readString();
        this.content = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.path);
        buffer.writeString(this.content);
    }
}
