package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketInAPIPlayerTitle extends Packet {

    private String title, subTitle, username;
    private int fadeIn, stay, fadeOut;

    public PacketInAPIPlayerTitle() {
        setPacketUUID(2345621);
    }

    public PacketInAPIPlayerTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut, String username) {
        setPacketUUID(2345621);
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.username = username;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.title = buffer.readString();
        this.subTitle = buffer.readString();
        this.fadeIn = buffer.readInt();
        this.stay = buffer.readInt();
        this.fadeOut = buffer.readInt();
        this.username = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(this.title);
        buffer.writeString(this.subTitle);
        buffer.writeInt(this.fadeIn);
        buffer.writeInt(this.stay);
        buffer.writeInt(this.fadeOut);
        buffer.writeString(this.username);
    }
}
