package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import net.kyori.adventure.text.Component;

@Getter
public class PacketInCloudPlayerComponent extends Packet {

    private Component component;
    private String player;

    public PacketInCloudPlayerComponent() {
        setPacketUUID(935423342);
    }

    public PacketInCloudPlayerComponent(Component component, String player) {
        setPacketUUID(935423342);
        this.component = component;
        this.player = player;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.component = (Component) buffer.readClass(Component.class);
        this.player = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeClass(this.component);
        buffer.writeString(this.player);
    }
}
