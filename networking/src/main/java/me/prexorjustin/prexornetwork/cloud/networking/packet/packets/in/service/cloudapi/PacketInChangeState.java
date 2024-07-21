package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInChangeState extends Packet {

    private String service, state;

    public PacketInChangeState() {
        setPacketUUID(893221);
    }

    public PacketInChangeState(String service, String state) {
        setPacketUUID(893221);
        this.service = service;
        this.state = state;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.service = buffer.readString();
        this.state = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.service);
        buffer.writeString(this.state);
    }
}
