package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPIEnvironment;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceDisconnect;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class BungeeBootstrap extends Plugin {

    @Getter
    private static BungeeBootstrap instance;
    private BungeeAudiences audiences;

    @Override
    public void onEnable() {
        instance = this;
        audiences = BungeeAudiences.builder(instance).build();
    }

    @Override
    public void onLoad() {
        new CloudAPI();

        CloudAPIEnvironment apiEnvironment = new CloudAPIEnvironment();
        apiEnvironment.handleNettyConnection();
        apiEnvironment.registerHandlers();
        apiEnvironment.registerBungeeHandlers();
        apiEnvironment.handleNettyUpdate();
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> proxiedPlayer.disconnect(TextComponent.fromLegacy("cloudservice.shutdown")));

        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        NettyDriver.getInstance().getNettyClient().sendPacketsSynchronized(new PacketInServiceDisconnect(service.getService()));
    }
}
