package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutServiceLaunch extends Packet {

    private String name, group, node;
    private boolean isProxy;

    public PacketOutServiceLaunch() {
        setPacketUUID(904359092);
    }

    public PacketOutServiceLaunch(String name, boolean isProxy, String group, String node) {
        setPacketUUID(904359092);
        this.name = name;
        this.isProxy = isProxy;
        this.group = group;
        this.node = node;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.isProxy = buffer.readBoolean();
        this.node = buffer.readString();
        this.node = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeBoolean(this.isProxy);
        buffer.writeString(this.node);
        buffer.writeString(this.node);
    }
}
