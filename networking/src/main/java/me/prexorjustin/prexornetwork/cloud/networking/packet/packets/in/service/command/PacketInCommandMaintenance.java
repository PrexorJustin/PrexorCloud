package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInCommandMaintenance extends Packet {

    private String name;
    private boolean removed;

    public PacketInCommandMaintenance() {
        setPacketUUID(382145654);
    }

    public PacketInCommandMaintenance(String name, boolean removed) {
        setPacketUUID(382145654);
        this.name = name;
        this.removed = removed;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.name = buffer.readString();
        this.removed = buffer.readBoolean();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeBoolean(this.removed);
    }
}
