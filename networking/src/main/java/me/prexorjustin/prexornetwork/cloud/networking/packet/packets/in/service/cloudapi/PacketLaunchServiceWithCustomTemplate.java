package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import org.jetbrains.annotations.NotNull;

@Getter
public class PacketLaunchServiceWithCustomTemplate extends Packet {

    private String group, template;

    public PacketLaunchServiceWithCustomTemplate() {
        setPacketUUID(439021234);
    }

    public PacketLaunchServiceWithCustomTemplate(String group, String template) {
        setPacketUUID(439021234);
        this.group = group;
        this.template = template;
    }

    @Override
    public void readPacket(@NotNull NettyBuffer buffer) {
        this.group = buffer.readString();
        this.template = buffer.readString();
    }

    @Override
    public void writePacket(@NotNull NettyBuffer buffer) {
        buffer.writeString(this.group);
        buffer.writeString(this.template);
    }
}
