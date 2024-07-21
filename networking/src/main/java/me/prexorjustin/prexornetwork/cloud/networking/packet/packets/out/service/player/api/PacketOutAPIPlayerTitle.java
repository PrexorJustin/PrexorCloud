package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

@Getter
public class PacketOutAPIPlayerTitle extends Packet {

    private String title, subTitle, username;
    private int fadeIn, stay, fadeOut;

    public PacketOutAPIPlayerTitle() {
        setPacketUUID(234112111);
    }

    public PacketOutAPIPlayerTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut, String username) {
        setPacketUUID(234112111);
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.username = username;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        title = buffer.readString();
        subTitle = buffer.readString();
        fadeIn = buffer.readInt();
        stay = buffer.readInt();
        fadeOut = buffer.readInt();
        username = buffer.readString();
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
