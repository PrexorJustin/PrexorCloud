package me.prexorjustin.prexornetwork.cloud.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.api.PluginDriver;
import me.prexorjustin.prexornetwork.cloud.plugin.velocity.command.VelocityCloudCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.velocity.command.VelocityEndCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.velocity.listener.VelocityCloudConnectListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.random.RandomGenerator;

@Getter
@Plugin(id = "prexorcloudplugin", name = "PrexorCloudPlugin", version = "1.0", authors = "PrexorJustin", dependencies = {@Dependency(id = "prexorcloudapi")})
public class VelocityBootstrap {

    @Getter
    private static VelocityBootstrap instance;

    private final ProxyServer proxyServer;
    private final MiniMessage miniMessage;

    @Inject
    public VelocityBootstrap(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;
        this.miniMessage = MiniMessage.builder().build();
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        new Driver();
        new PluginDriver();
        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);

        CloudAPI.getInstance().setState(ServiceState.LOBBY, service.getName());

        proxyServer.getCommandManager().register("cloud", new VelocityCloudCommand(), "prexorcloud", "pc");
        proxyServer.getCommandManager().register("end", new VelocityEndCommand());

        this.proxyServer.getEventManager().register(this, new VelocityCloudConnectListener());

        new TimerBase().schedule(new TimerTask() {
            @Override
            public void run() {
                if (CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup()).isMaintenance()) {
                    proxyServer.getAllPlayers().forEach(player -> {
                        if (!player.hasPermission("prexorcloud.bypass.connection.maintenance") && !CloudAPI.getInstance().getWhitelist().contains(player.getUsername())) {
                            player.disconnect(Component.text(
                                    Driver.getInstance().getMessageStorage().base64ToUTF8(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNetworkIsMaintenance")).replace("&", "§")
                            ));
                        }
                    });
                }

                if (!NettyDriver.getInstance().getNettyClient().getChannel().isActive()) System.exit(0);
            }
        }, 1, 1, TimeUtil.SECONDS);
    }

    public CloudService getLobby(Player player) {
        if (CloudAPI.getInstance().getServicePool().getConnectedServices().isEmpty()) return null;
        else if (CloudAPI.getInstance().getServicePool().getConnectedServices().stream().noneMatch(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY") && cloudService.getState() == ServiceState.LOBBY))
            return null;
        else {
            List<CloudService> lobby = CloudAPI.getInstance().getServicePool().getConnectedServices().stream()
                    .filter(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY"))
                    .filter(cloudService -> {
                        if (!cloudService.getGroup().isMaintenance()) return true;
                        else
                            return player.hasPermission("prexorcloud.maintenance.bypass") || CloudAPI.getInstance().getWhitelist().contains(player.getUsername());
                    })
                    .filter(cloudService -> cloudService.getState() == ServiceState.LOBBY)
                    .toList().stream().filter(cloudService -> {
                        if (cloudService.getGroup().getPermission().equalsIgnoreCase("")) return true;
                        else return player.hasPermission(cloudService.getGroup().getPermission());
                    }).map(CloudService.class::cast).toList();

            if (lobby.isEmpty()) return null;

            ArrayList<Integer> priorities = new ArrayList<>();
            lobby.forEach(cloudService -> priorities.add(cloudService.getGroup().getPriority()));
            priorities.sort(Collections.reverseOrder());

            Integer priority = priorities.getFirst();
            List<CloudService> prioritizedLobbys = lobby.stream().filter(cloudService -> Objects.equals(cloudService.getGroup().getPriority(), priority)).toList();

            return prioritizedLobbys.get(RandomGenerator.getDefault().nextInt(prioritizedLobbys.size()));
        }
    }

    public CloudService getLobby(Player player, String kicked) {
        if (CloudAPI.getInstance().getServicePool().getConnectedServices().isEmpty()) return null;
        else if (CloudAPI.getInstance().getServicePool().getConnectedServices().stream().noneMatch(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY") && cloudService.getState() == ServiceState.LOBBY))
            return null;
        else {
            List<CloudService> lobby = CloudAPI.getInstance().getServicePool().getConnectedServices().stream()
                    .filter(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY"))
                    .filter(cloudService -> {
                        if (!cloudService.getGroup().isMaintenance()) return true;
                        else
                            return player.hasPermission("prexorcloud.maintenance.bypass") || CloudAPI.getInstance().getWhitelist().contains(player.getUsername());
                    })
                    .filter(cloudService -> cloudService.getName().equalsIgnoreCase(kicked))
                    .filter(cloudService -> cloudService.getState() == ServiceState.LOBBY)
                    .filter(cloudService -> cloudService.getGroup().getPermission().equalsIgnoreCase("") || player.hasPermission(cloudService.getGroup().getPermission()))
                    .map(CloudService.class::cast).toList();

            if (lobby.isEmpty()) return null;

            ArrayList<Integer> priorities = new ArrayList<>();
            lobby.forEach(cloudService -> priorities.add(cloudService.getGroup().getPriority()));
            priorities.sort(Collections.reverseOrder());

            Integer priority = priorities.getFirst();
            List<CloudService> prioritizedLobbys = lobby.stream().filter(cloudService -> Objects.equals(cloudService.getGroup().getPriority(), priority)).toList();

            return prioritizedLobbys.get(RandomGenerator.getDefault().nextInt(prioritizedLobbys.size()));
        }
    }
}
