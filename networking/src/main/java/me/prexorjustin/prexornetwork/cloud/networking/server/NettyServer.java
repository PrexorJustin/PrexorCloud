package me.prexorjustin.prexornetwork.cloud.networking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketDecoder;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketEncoder;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketLengthDeserializer;
import me.prexorjustin.prexornetwork.cloud.networking.codec.PacketLengthSerializer;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServer extends ChannelInitializer<Channel> implements AutoCloseable {

    private final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>();
    EventLoopGroup BOSS;
    EventLoopGroup WORKER;
    private int port;
    private ChannelFuture channelFuture;

    public NettyServer bind(final int port) {
        this.port = port;
        return this;
    }

    @SneakyThrows
    public void start() {
        boolean isEpoll = Epoll.isAvailable();

        this.BOSS = isEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.WORKER = isEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        this.channelFuture = new ServerBootstrap()
                .group(BOSS, WORKER)
                .channel(isEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(this)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .bind(new InetSocketAddress(port))
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .sync().channel().closeFuture();
    }

    public void registerChannel(String receiver, Channel channel) {
        if (!isChannelRegistered(receiver))
            this.CHANNELS.put(receiver, channel);
    }

    public boolean isChannelRegistered(String receiver) {
        return this.CHANNELS.containsKey(receiver);
    }

    public void removeChannel(String receiver) {
        if (this.CHANNELS.get(receiver).isActive()) {
            this.CHANNELS.get(receiver).close();
            this.CHANNELS.remove(receiver);
        }
    }

    public void close() {
        this.CHANNELS.forEach((s, channel) -> channel.close());
        this.channelFuture.cancel(true);
        this.BOSS.shutdownGracefully();
        this.WORKER.shutdownGracefully();
    }

    @Override
    protected void initChannel(Channel channel) {
        final InetSocketAddress inetSocketAddress = ((InetSocketAddress) channel.remoteAddress());

        if (isAddressAllowed(inetSocketAddress.getAddress().getHostAddress())) {
            channel.pipeline()
                    .addLast("packet-length-deserializer", new PacketLengthDeserializer())
                    .addLast("packet-decoder", new PacketDecoder())
                    .addLast("packet-length-serializer", new PacketLengthSerializer())
                    .addLast("packet-encoder", new PacketEncoder())
                    .addLast("worker", new NettyServerWorker());
        } else {
            channel.close().addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    private boolean isAddressAllowed(String address) {
        return NettyDriver.getInstance().getAllowedAddresses().contains(address);
    }

    public void sendToAllSynchronized(final Packet packet) {
        this.CHANNELS.forEach((s, channel) -> channel.writeAndFlush(packet));
    }

    public void sendToAllSynchronized(final Packet... packets) {
        Arrays.stream(packets).forEach(this::sendToAllSynchronized);
    }

    public void sendPacketSynchronized(final String channel, final Packet packet) {
        this.CHANNELS.get(channel).writeAndFlush(packet);
    }

    public void sendPacketSynchronized(final String channel, final Packet... packets) {
        Arrays.stream(packets).forEach(packet -> this.CHANNELS.get(channel).writeAndFlush(packet));
    }

    public void sendToAllAsynchronous(final Packet packet) {
        new Thread(() -> this.CHANNELS.forEach((s, channel) -> channel.writeAndFlush(packet))).start();
    }

    public void sendToAllAsynchronous(final Packet... packets) {
        Arrays.stream(packets).forEach(this::sendToAllAsynchronous);
    }

    public void sendPacketAsynchronous(final String channel, final Packet packet) {
        new Thread(() -> this.CHANNELS.get(channel).writeAndFlush(packet)).start();
    }

    public void sendPacketAsynchronous(final String channel, final Packet... packets) {
        new Thread(() -> Arrays.stream(packets).forEach(packet -> this.CHANNELS.get(channel).writeAndFlush(packet))).start();
    }
}
