package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAPIPlayerTab extends Packet {

    private String username, header, footer;

    public PacketInAPIPlayerTab() {
        setPacketUUID(2143290823);
    }

    public PacketInAPIPlayerTab(String username, String header, String footer) {
        setPacketUUID(2143290823);
        this.username = username;
        this.header = header;
        this.footer = footer;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.username = buffer.readString();
        this.header = buffer.readString();
        this.footer = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.header);
        buffer.writeString(this.footer);
    }
}
