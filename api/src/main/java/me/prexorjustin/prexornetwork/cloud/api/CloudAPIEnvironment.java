package me.prexorjustin.prexornetwork.cloud.api;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.listener.BungeeCloudEvents;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.networking.*;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.listener.VelocityCloudEvents;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.velocity.networking.*;
import me.prexorjustin.prexornetwork.cloud.api.networking.*;
import me.prexorjustin.prexornetwork.cloud.api.player.async.entrys.AsyncCloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.player.sync.entrys.CloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.storage.uuid.UUIDDriver;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServiceList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.client.NettyClient;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupCreate;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupDelete;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupEdit;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.*;

import java.util.TimerTask;
import java.util.UUID;

@NoArgsConstructor
public class CloudAPIEnvironment {

    public void handleNettyConnection() {
        new NettyDriver();

        NettyDriver.getInstance().setNettyClient(new NettyClient());
        NettyDriver.getInstance().getNettyClient().bind(
                CloudAPI.getInstance().getService().getManagerAddress(),
                CloudAPI.getInstance().getService().getNetworkingPort()
        ).connect();

        registerAllPlayerFromRestAPI();
    }

    public void handleNettyUpdate() {
        String serviceName = CloudAPI.getInstance().getService().getName();
        NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(new PacketInServiceConnect(serviceName));

        new TimerBase().scheduleAsync(new TimerTask() {
            @Override
            public void run() {
                CloudService service = CloudAPI.getInstance().getServicePool().getService(serviceName);
                LiveServiceList liveServiceList = (LiveServiceList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.CLOUDSERVICE_GENERAL.getRoute()), LiveServiceList.class);
                PlayerGeneral players = (PlayerGeneral) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.PLAYER_GENERAL.getRoute()), PlayerGeneral.class);

                registerAllPlayerFromRestAPI();

                CloudAPI.getInstance().getPlayerPool().getConnectedPlayers().stream().filter(cloudPlayer -> players.getPlayers().stream().noneMatch(s -> s.equalsIgnoreCase(cloudPlayer.getUuid().toString()))).toList().forEach(cloudPlayer -> {
                    CloudAPI.getInstance().getPlayerPool().unregisterPlayer(cloudPlayer.getUuid());
                    CloudAPI.getInstance().getAsyncPlayerPool().unregisterPlayer(cloudPlayer.getUuid());
                });

                CloudAPI.getInstance().getServicePool().getConnectedServices().stream().filter(cloudService -> liveServiceList.getCloudServices().stream().noneMatch(s -> s.equalsIgnoreCase(cloudService.getName()))).toList().forEach(cloudService -> {
                    CloudAPI.getInstance().getServicePool().unregisterService(cloudService.getName());
                    CloudAPI.getInstance().getAsyncServicePool().unregisterService(cloudService.getName());
                });

                if (!NettyDriver.getInstance().getNettyClient().getChannel().isOpen()) System.exit(0);

                String route = WebServer.Routes.CLOUDSERVICE.getRoute().replace("%servicename%", service.getName().replace(liveServiceList.getCloudServiceSplitter(), "~"));

                LiveServices liveServices = (LiveServices) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(route), LiveServices.class);
                liveServices.setLastReaction(System.currentTimeMillis());

                CloudAPI.getInstance().getRestDriver().update(route, new ConfigDriver().convert(liveServices));
            }
        }, 30, 30, TimeUtil.SECONDS);
    }

    public void registerHandlers() {
        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutCloudServiceCouldNotStartEvent().getPacketUUID(), new HandlePacketOutCloudService(), PacketOutCloudServiceCouldNotStartEvent.class)
                .registerHandler(new PacketOutCloudProxyCouldNotStartEvent().getPacketUUID(), new HandlePacketOutCloudService(), PacketOutCloudProxyCouldNotStartEvent.class)
                .registerHandler(new PacketOutServicePrepared().getPacketUUID(), new HandlePacketOutService(), PacketOutServicePrepared.class)
                .registerHandler(new PacketOutServiceConnected().getPacketUUID(), new HandlePacketOutService(), PacketOutServiceConnected.class)
                .registerHandler(new PacketOutServiceDisconnected().getPacketUUID(), new HandlePacketOutService(), PacketOutServiceDisconnected.class)
                .registerHandler(new PacketOutPlayerConnect().getPacketUUID(), new HandlePacketOutPlayer(), PacketOutPlayerConnect.class)
                .registerHandler(new PacketOutPlayerDisconnect().getPacketUUID(), new HandlePacketOutPlayer(), PacketOutPlayerDisconnect.class)
                .registerHandler(new PacketOutPlayerSwitchService().getPacketUUID(), new HandlePacketOutPlayer(), PacketOutPlayerSwitchService.class)
                .registerHandler(new PacketOutServiceLaunch().getPacketUUID(), new HandlePacketOutService(), PacketOutServiceLaunch.class)
                .registerHandler(new PacketOutGroupCreate().getPacketUUID(), new HandlePacketOutGroup(), PacketOutGroupCreate.class)
                .registerHandler(new PacketOutGroupDelete().getPacketUUID(), new HandlePacketOutGroup(), PacketOutGroupDelete.class)
                .registerHandler(new PacketOutGroupEdit().getPacketUUID(), new HandlePacketOutGroup(), PacketOutGroupEdit.class)
                .registerHandler(new PacketOutCloudRestAPIReloadEvent().getPacketUUID(), new HandlePacketOutRestAPI(), PacketOutCloudRestAPIReloadEvent.class)
                .registerHandler(new PacketOutCloudRestAPICreateEvent().getPacketUUID(), new HandlePacketOutRestAPI(), PacketOutCloudRestAPICreateEvent.class)
                .registerHandler(new PacketOutCloudRestAPIUpdateEvent().getPacketUUID(), new HandlePacketOutRestAPI(), PacketOutCloudRestAPIUpdateEvent.class)
                .registerHandler(new PacketOutCloudRestAPIDeleteEvent().getPacketUUID(), new HandlePacketOutRestAPI(), PacketOutCloudRestAPIDeleteEvent.class)
                .registerHandler(new PacketOutCloudServiceChangeState().getPacketUUID(), new HandlePacketOutCloudService(), PacketOutCloudServiceChangeState.class)
                .registerHandler(new PacketOutCloudProxyChangeState().getPacketUUID(), new HandlePacketOutCloudService(), PacketOutCloudProxyChangeState.class);
    }

    public void registerBungeeHandlers() {
        CloudAPI.getInstance().getEventDriver().registerListener(new BungeeCloudEvents());

        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutAPIPlayerConnect().getPacketUUID(), new HandleBungeePacketOutAPIPlayerConnect(), PacketOutAPIPlayerConnect.class)
                .registerHandler(new PacketOutAPIPlayerMessage().getPacketUUID(), new HandleBungeePacketOutAPIPlayerMessage(), PacketOutAPIPlayerMessage.class)
                .registerHandler(new PacketOutAPIPlayerTab().getPacketUUID(), new HandleBungeePacketOutAPIPlayerTab(), PacketOutAPIPlayerTab.class)
                .registerHandler(new PacketOutCloudPlayerComponent().getPacketUUID(), new HandleBungeePacketOutCloudPlayerComponent(), PacketOutCloudPlayerComponent.class)
                .registerHandler(new PacketOutAPIPlayerKick().getPacketUUID(), new HandleBungeePacketOutAPIPlayerKick(), PacketOutAPIPlayerKick.class)
                .registerHandler(new PacketOutAPIPlayerTitle().getPacketUUID(), new HandleBungeePacketOutAPIPlayerTitle(), PacketOutAPIPlayerTitle.class)
                .registerHandler(new PacketOutAPIPlayerDispatchCommand().getPacketUUID(), new HandleBungeePacketOutAPIPlayerDispatchCommand(), PacketOutAPIPlayerDispatchCommand.class)
                .registerHandler(new PacketOutAPIPlayerActionBar().getPacketUUID(), new HandleBungeePacketOutAPIPlayerActionBar(), PacketOutAPIPlayerActionBar.class);
    }

    public void registerVelocityHandlers() {
        CloudAPI.getInstance().getEventDriver().registerListener(new VelocityCloudEvents());

        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutAPIPlayerConnect().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerConnect(), PacketOutAPIPlayerConnect.class)
                .registerHandler(new PacketOutAPIPlayerMessage().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerMessage(), PacketOutAPIPlayerMessage.class)
                .registerHandler(new PacketOutAPIPlayerTab().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerTab(), PacketOutAPIPlayerTab.class)
                .registerHandler(new PacketOutCloudPlayerComponent().getPacketUUID(), new HandleVelocityPacketOutCloudPlayerComponent(), PacketOutCloudPlayerComponent.class)
                .registerHandler(new PacketOutAPIPlayerKick().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerKick(), PacketOutAPIPlayerKick.class)
                .registerHandler(new PacketOutAPIPlayerTitle().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerTitle(), PacketOutAPIPlayerTitle.class)
                .registerHandler(new PacketOutAPIPlayerDispatchCommand().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerDispatchCommand(), PacketOutAPIPlayerDispatchCommand.class)
                .registerHandler(new PacketOutAPIPlayerActionBar().getPacketUUID(), new HandleVelocityPacketOutAPIPlayerActionBar(), PacketOutAPIPlayerActionBar.class);
    }

    private void registerAllPlayerFromRestAPI() {
        PlayerGeneral players = (PlayerGeneral) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/general"), PlayerGeneral.class);

        players.getPlayers().forEach(player -> {
            if (CloudAPI.getInstance().getPlayerPool().isPlayerNull(player)) {
                UUID uuid = UUIDDriver.getUUID(player);
                CloudAPI.getInstance().getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(player, uuid));
                CloudAPI.getInstance().getPlayerPool().registerPlayer(new CloudPlayer(player, uuid));
            }
        });
    }
}
