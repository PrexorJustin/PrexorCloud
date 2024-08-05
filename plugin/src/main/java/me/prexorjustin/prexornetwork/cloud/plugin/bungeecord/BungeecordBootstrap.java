package me.prexorjustin.prexornetwork.cloud.plugin.bungeecord;

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
import me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.command.BungeecordCloudCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.command.BungeecordEndCommand;
import me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.listener.BungeecordCloudConnectListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;
import java.util.random.RandomGenerator;

public class BungeecordBootstrap extends Plugin {

    @Getter
    private static BungeecordBootstrap instance;

    @Override
    public void onEnable() {
        new Driver();
        new PluginDriver();

        instance = this;

        LiveService service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);

        CloudAPI.getInstance().setState(ServiceState.LOBBY, service.getName());

        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeecordCloudConnectListener());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeecordCloudCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeecordEndCommand());

        new TimerBase().schedule(new TimerTask() {
            @Override
            public void run() {
                if (CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup()).isMaintenance()) {
                    ProxyServer.getInstance().getPlayers().forEach(player -> {
                        if (!player.hasPermission("prexorcloud.bypass.connection.maintenance") && !CloudAPI.getInstance().getWhitelist().contains(player.getName())) {
                            player.disconnect(TextComponent.fromLegacyText(
                                    Driver.getInstance().getMessageStorage().base64ToUTF8(CloudAPI.getInstance().getMessageConfig().getMessages().get("kickNetworkIsMaintenance")).replace("&", "§")
                            ));
                        }
                    });
                }

                if (!NettyDriver.getInstance().getNettyClient().getChannel().isActive()) System.exit(0);
            }
        }, 2, 2, TimeUtil.SECONDS);
    }

    public CloudService getLobby(ProxiedPlayer player) {
        if (CloudAPI.getInstance().getServicePool().getConnectedServices().isEmpty()) return null;
        else if (CloudAPI.getInstance().getServicePool().getConnectedServices().stream().noneMatch(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY") && cloudService.getState() == ServiceState.LOBBY))
            return null;
        else {
            List<CloudService> lobby = CloudAPI.getInstance().getServicePool().getConnectedServices().stream()
                    .filter(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY"))
                    .filter(cloudService -> {
                        if (!cloudService.getGroup().isMaintenance()) return true;
                        else
                            return player.hasPermission("prexorcloud.maintenance.bypass") || CloudAPI.getInstance().getWhitelist().contains(player.getName());
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

    public CloudService getLobby(ProxiedPlayer player, String kicked) {
        if (CloudAPI.getInstance().getServicePool().getConnectedServices().isEmpty()) return null;
        else if (CloudAPI.getInstance().getServicePool().getConnectedServices().stream().noneMatch(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY") && cloudService.getState() == ServiceState.LOBBY))
            return null;
        else {
            List<CloudService> lobby = CloudAPI.getInstance().getServicePool().getConnectedServices().stream()
                    .filter(cloudService -> cloudService.getGroup().getGroupType().equalsIgnoreCase("LOBBY"))
                    .filter(cloudService -> {
                        if (!cloudService.getGroup().isMaintenance()) return true;
                        else
                            return player.hasPermission("prexorcloud.maintenance.bypass") || CloudAPI.getInstance().getWhitelist().contains(player.getName());
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
