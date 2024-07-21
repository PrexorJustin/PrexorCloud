package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInLaunchService extends Packet {

    private String group;

    public PacketInLaunchService() {
        setPacketUUID(92123);
    }

    public PacketInLaunchService(String group) {
        setPacketUUID(92123);
        this.group = group;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.group = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.group);
    }
}
