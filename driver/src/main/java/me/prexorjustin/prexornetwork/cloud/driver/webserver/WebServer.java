package me.prexorjustin.prexornetwork.cloud.driver.webserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.authentication.AuthenticatorKey;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.handle.RequestHandler;

import java.util.concurrent.ConcurrentLinkedDeque;

public class WebServer {

    private final ConcurrentLinkedDeque<RouteEntry> ROUTES;
    private final EventLoopGroup bossGroup, workerGroup;
    private final Thread current;
    public String AUTH_KEY;

    @SneakyThrows
    public WebServer() {
        AuthenticatorKey authenticatorKey = (AuthenticatorKey) new ConfigDriver("./connection.key").read(AuthenticatorKey.class);
        this.AUTH_KEY = Driver.getInstance().getMessageStorage().base64ToUTF8(authenticatorKey.getKey());
        this.ROUTES = new ConcurrentLinkedDeque<>();
        ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        this.current = new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<>() {
                            @Override
                            protected void initChannel(Channel channel) {
                                channel.pipeline().addLast(new ChannelHandler() {
                                    @Override
                                    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {

                                    }

                                    @Override
                                    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {

                                    }
                                });

                                channel.pipeline().addLast(new HttpRequestDecoder());
                                channel.pipeline().addLast(new HttpResponseEncoder());
                                channel.pipeline().addLast(new HttpObjectAggregator(65536));
                                channel.pipeline().addLast(new RequestHandler());
                            }
                        });

                ChannelFuture future = bootstrap.bind(config.getRestPort()).sync();
                Channel channel = future.channel();
                channel.closeFuture().sync();
            } catch (Exception ignored) {

            }
        });
        current.start();
    }

    public String getRoute(String path) {
        if (ROUTES.parallelStream().noneMatch(routeEntry -> routeEntry.readROUTE().equalsIgnoreCase(path)))
            return null;
        else
            return ROUTES.parallelStream().filter(routeEntry -> routeEntry.readROUTE().equalsIgnoreCase(path)).findFirst().get().channelRead();
    }

    public RouteEntry getRoutes(String path) {
        return ROUTES.parallelStream().filter(routeEntry -> routeEntry.readROUTE().equalsIgnoreCase(path)).findFirst().orElse(null);
    }

    public boolean doesContentExist(String path) {
        return getRoute(path) != null;
    }


    public void addRoute(RouteEntry entry) {
        ROUTES.add(entry);
    }

    public void updateRoute(String path, String json) {
        this.ROUTES.parallelStream().filter(routeEntry -> routeEntry.route.equalsIgnoreCase(path)).findFirst().get().channelUpdate(json);
    }

    public void removeRoute(String path) {
        this.ROUTES.removeIf(entry -> entry.route.equalsIgnoreCase(path));
    }

    public void close() {
        current.interrupt();
        current.interrupt();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
