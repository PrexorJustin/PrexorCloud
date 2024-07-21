package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.listener;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.VelocityBootstrap;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.ICloudListener;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Priority;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Subscribe;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceDisconnectedEvent;

import java.net.InetSocketAddress;

public class VelocityCloudEvents implements ICloudListener {


    @Subscribe(priority = Priority.HIGHEST)
    public void handle(CloudServiceConnectedEvent event) {
        VelocityBootstrap.getInstance().getProxyServer().registerServer(
                new ServerInfo(event.getName(), new InetSocketAddress(event.getHost(), event.getPort()))
        );
    }

    @Subscribe
    public void handle(CloudServiceDisconnectedEvent event) {
        RegisteredServer server = VelocityBootstrap.getInstance().getProxyServer().getServer(event.getName()).orElse(null);
        assert server != null;

        VelocityBootstrap.getInstance().getProxyServer().unregisterServer(server.getServerInfo());
    }
}
