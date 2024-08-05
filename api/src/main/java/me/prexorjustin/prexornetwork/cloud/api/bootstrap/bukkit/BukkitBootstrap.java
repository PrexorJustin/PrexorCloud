package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bukkit;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPIEnvironment;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceDisconnect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        new CloudAPI();

        CloudAPIEnvironment apiEnvironment = new CloudAPIEnvironment();
        apiEnvironment.handleNettyConnection();
        apiEnvironment.registerHandlers();
        apiEnvironment.handleNettyUpdate();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("cloudservice-shutdown"));
        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        NettyDriver.getInstance().getNettyClient().sendPacketsSynchronized(new PacketInServiceDisconnect(service.getName()));
    }
}
