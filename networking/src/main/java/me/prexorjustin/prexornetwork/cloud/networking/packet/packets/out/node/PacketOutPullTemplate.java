package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutPullTemplate extends Packet {

    private String url, template;

    public PacketOutPullTemplate() {
        setPacketUUID(8732742);
    }

    public PacketOutPullTemplate(String url, String template) {
        setPacketUUID(8732742);
        this.url = url;
        this.template = template;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.url = buffer.readString();
        this.template = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.url);
        buffer.writeString(this.template);
    }
}
