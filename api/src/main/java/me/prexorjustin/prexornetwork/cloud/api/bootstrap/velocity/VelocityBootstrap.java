package me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPIEnvironment;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceDisconnect;
import net.kyori.adventure.text.Component;


@Getter
@Plugin(id = "prexorcloudapi", name = "PrexorCloudAPI", version = "1.0", authors = "PrexorJustin")
public class VelocityBootstrap {

    @Getter
    private static VelocityBootstrap instance;

    private final ProxyServer proxyServer;

    @Inject
    public VelocityBootstrap(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        new CloudAPI();

        CloudAPIEnvironment apiEnvironment = new CloudAPIEnvironment();
        apiEnvironment.handleNettyConnection();
        apiEnvironment.registerHandlers();
        apiEnvironment.registerVelocityHandlers();
        apiEnvironment.handleNettyUpdate();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        proxyServer.getAllPlayers().forEach(player -> player.disconnect(Component.text("cloudservice-shutdown")));

        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        NettyDriver.getInstance().getNettyClient().sendPacketsSynchronized(new PacketInServiceDisconnect(service.getName()));
    }

}
