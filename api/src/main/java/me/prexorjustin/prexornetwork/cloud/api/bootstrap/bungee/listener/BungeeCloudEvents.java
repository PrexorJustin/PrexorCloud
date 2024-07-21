package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.listener;

import me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.utils.ServerDriverUtil;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.ICloudListener;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Priority;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Subscribe;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceDisconnectedEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class BungeeCloudEvents implements ICloudListener {

    @Subscribe(priority = Priority.HIGHEST)
    public void handleCloudServiceConnect(CloudServiceConnectedEvent event) {
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
                event.getName(),
                new InetSocketAddress(event.getHost(), event.getPort()),
                "prexorcloud-service",
                false
        );

        ServerDriverUtil.addServer(serverInfo);
    }


    @Subscribe(priority = Priority.HIGHEST)
    public void handleCloudServiceDisconnect(CloudServiceDisconnectedEvent event) {
        ServerDriverUtil.removeServer(event.getName());
    }
}
