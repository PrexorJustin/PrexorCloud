package me.prexorjustin.prexornetwork.cloud.networking.packet;

import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class PacketDriver {

    private final Map<Integer, Class<? extends Packet>> packets = new ConcurrentHashMap<>();
    private final Map<Integer, NettyAdaptor> adaptor = new ConcurrentHashMap<>();

    public void call(@NotNull Integer id, @NotNull Channel channel, @NotNull Packet packet) {
        NettyAdaptor nettyAdaptor = adaptor.get(id);
        if (nettyAdaptor != null) nettyAdaptor.handle(channel, packet);
    }

    public PacketDriver registerHandler(Integer id, NettyAdaptor nettyAdaptor, Class<? extends Packet> packet) {
        adaptor.putIfAbsent(id, nettyAdaptor);

        try {
            Packet packetInstance = packet.getDeclaredConstructor().newInstance();
            packets.putIfAbsent(packetInstance.getPacketUUID(), packet);
        } catch (Exception ignored) {
        }

        return this;
    }

    public Class<? extends Packet> getPacket(Integer id) {
        return packets.getOrDefault(id, null);
    }
}
