package me.prexorjustin.prexornetwork.cloud.plugin.bukkit;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.api.PluginDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.ServiceCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.StopCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.implementation.InfoCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.command.implementation.ShutdownCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.listener.ReloadListener;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.listener.ServiceListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.TimerTask;

@Getter
public class BukkitBootstrap extends JavaPlugin {

    @Getter
    private static BukkitBootstrap instance;

    private LiveService service;

    @Override
    public void onEnable() {
        new Driver();
        new PluginDriver();

        instance = this;
        this.service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);

        CloudAPI.getInstance().setState(ServiceState.LOBBY, this.service.getName());

        Bukkit.getPluginManager().registerEvents(new ReloadListener(), this);
        Bukkit.getPluginManager().registerEvents(new ServiceListener(), this);

        Objects.requireNonNull(getCommand("service")).setExecutor(new ServiceCommand());
        Objects.requireNonNull(getCommand("stop")).setExecutor(new StopCommand());
        PluginDriver.getInstance().registerCommand(new InfoCommand());
        PluginDriver.getInstance().registerCommand(new ShutdownCommand());

        new TimerBase().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!NettyDriver.getInstance().getNettyClient().getChannel().isActive()) {
                    System.out.println("This ain't supposed to happen bro");
                    System.exit(0);
                }
            }
        }, 10, 10, TimeUtil.SECONDS);
    }
}
