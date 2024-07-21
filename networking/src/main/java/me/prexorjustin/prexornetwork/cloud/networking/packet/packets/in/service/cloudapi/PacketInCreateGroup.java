package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInCreateGroup extends Packet {

    private String groupConfig;

    public PacketInCreateGroup() {
        setPacketUUID(398293);
    }

    public PacketInCreateGroup(String groupConfig) {
        setPacketUUID(398293);
        this.groupConfig = groupConfig;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.groupConfig = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.groupConfig);
    }
}
