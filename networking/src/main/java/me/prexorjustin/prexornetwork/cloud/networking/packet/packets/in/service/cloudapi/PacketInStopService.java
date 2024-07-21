package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInStopService extends Packet {

    private String service;

    public PacketInStopService() {
        setPacketUUID(1278782187);
    }

    public PacketInStopService(String service) {
        setPacketUUID(1278782187);
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.service);
    }
}
