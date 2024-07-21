package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutCloudServiceChangeState extends Packet {

    private String name, state;

    public PacketOutCloudServiceChangeState() {
        setPacketUUID(983429809);
    }

    public PacketOutCloudServiceChangeState(String name, String state) {
        setPacketUUID(983429809);
        this.name = name;
        this.state = state;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.state = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.state);
    }
}