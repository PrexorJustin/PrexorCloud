package me.prexorjustin.prexornetwork.cloud.networking.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketDecoder;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketEncoder;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketLengthDeserializer;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketLengthSerializer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class NettyClient extends ChannelInitializer<Channel> implements AutoCloseable {

    private int port;
    private String host;

    @Getter
    private Channel channel;
    private EventLoopGroup BOSS;

    public NettyClient bind(String host, int port) {
        this.port = port;
        this.host = host;
        return this;
    }

    public void connect() {
        boolean isEpoll = Epoll.isAvailable();

        BOSS = isEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            this.channel = new Bootstrap()
                    .group(BOSS)
                    .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.AUTO_READ, true)
                    .handler(this)
                    .connect(new InetSocketAddress(host, port))
                    .syncUninterruptibly()
                    .sync().channel();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void close() throws Exception {
        channel.close();
        BOSS.shutdownGracefully();
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast("packet-length-deserializer", new PacketLengthDeserializer())
                .addLast("packet-decoder", new PacketDecoder())
                .addLast("packet-length-serializer", new PacketLengthSerializer())
                .addLast("packet-encoder", new PacketEncoder())
                .addLast("worker", new NettyClientWorker());
    }

    private boolean isAddressAllowed(String address) {
        return NettyDriver.getInstance().getAllowedAddresses().contains(address);
    }

    public void sendPacketSynchronized(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void sendPacketsSynchronized(final Packet... packets) {
        Arrays.stream(packets).forEach(this::sendPacketSynchronized);
    }

    public void sendPacketAsynchronous(Packet packet) {
        new Thread(() -> {
            channel.writeAndFlush(packet);
            Thread.currentThread().interrupt();
        }).start();
    }

    public void sendPacketsAsynchronous(final Packet... packets) {
        Arrays.stream(packets).forEach(this::sendPacketsAsynchronous);
    }
}
