package me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyBuffer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import net.kyori.adventure.text.Component;

@Getter
public class PacketOutCloudPlayerComponent extends Packet {

    private Component component;
    private String username;


    public PacketOutCloudPlayerComponent() {
        setPacketUUID(234893298);
    }

    public PacketOutCloudPlayerComponent(Component component, String username) {
        setPacketUUID(234893298);
        this.component = component;
        this.username = username;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        this.component = (Component) buffer.readClass(Component.class);
        this.username = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeClass(this.component);
        buffer.writeString(this.username);
    }
}
