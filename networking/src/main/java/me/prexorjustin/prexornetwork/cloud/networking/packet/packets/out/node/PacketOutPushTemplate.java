package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

public class PacketOutPushTemplate extends Packet {


    private String url, template;

    public PacketOutPushTemplate() {
        setPacketUUID(87388742);
    }

    public PacketOutPushTemplate(String url, String template) {
        setPacketUUID(87388742);
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
