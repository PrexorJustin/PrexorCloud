package me.prexorjustin.prexornetwork.cloud.networking;

import lombok.Getter;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.networking.client.NettyClient;
import me.prexorjustin.prexornetwork.cloud.networking.packet.PacketDriver;
import me.prexorjustin.prexornetwork.cloud.networking.server.NettyServer;

import java.util.ArrayList;

@Getter
public class NettyDriver {

    @Getter
    private static NettyDriver instance;
    private final PacketDriver packetDriver;
    private ArrayList<String> allowedAddresses;

    @Setter
    private NettyClient nettyClient;
    private NettyServer nettyServer;

    public NettyDriver() {
        instance = this;

        packetDriver = new PacketDriver();
        nettyClient = new NettyClient();
    }
}
