package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutLaunchService extends Packet {

    private String service, group;
    private boolean useProtocol;

    public PacketOutLaunchService() {
        setPacketUUID(290938191);
    }

    public PacketOutLaunchService(String service, String group, boolean useProtocol) {
        setPacketUUID(290938191);
        this.service = service;
        this.group = group;
        this.useProtocol = useProtocol;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.service = buffer.readString();
        this.group = buffer.readString();
        this.useProtocol = buffer.readBoolean();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.service);
        buffer.writeString(this.group);
        buffer.writeBoolean(this.useProtocol);
    }
}
