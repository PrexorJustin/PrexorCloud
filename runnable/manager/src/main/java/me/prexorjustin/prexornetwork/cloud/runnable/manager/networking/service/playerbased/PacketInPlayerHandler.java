package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service.playerbased;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.CloudPlayerRestCache;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCache;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCacheConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerDisconnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.player.CloudPlayerSwitchEvent;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.storage.uuid.UUIDDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.PacketInPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInCloudPlayerComponent;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.PacketOutPlayerSwitchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutCloudPlayerComponent;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.Objects;

public class PacketInPlayerHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInPlayerConnect packetCast) {
            if (!PrexorCloudManager.shutdown) {
                String service = packetCast.getProxy();

                PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(WebServer.Routes.PLAYER_GENERAL.getRoute()), PlayerGeneral.class);
                general.getPlayers().removeIf(s -> s.equalsIgnoreCase(UUIDDriver.getUUID(packetCast.getName()).toString()));
                general.getPlayers().add(UUIDDriver.getUUID(packetCast.getName()).toString());
                Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.PLAYER_GENERAL.getRoute(), new ConfigDriver().convert(general));

                CloudPlayerRestCache cache = new CloudPlayerRestCache(packetCast.getName(), UUIDDriver.getUUID(packetCast.getName()).toString());
                cache.handleConnect(service);
                cache.setService("");
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName()), (new RestDriver()).convert(cache)));

                NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutPlayerConnect(packetCast.getName(), service));
                Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudPlayerConnectedEvent(packetCast.getName(), service, UUIDDriver.getUUID(packetCast.getName())));

                if (PrexorCloudManager.config.isShowConnectingPlayers()) {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.NETWORK,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-player-connect")
                                    .replace("%player%", packetCast.getName())
                                    .replace("%uuid%", Objects.requireNonNull(UUIDDriver.getUUID(packetCast.getName()).toString()))
                                    .replace("%proxy%", packetCast.getProxy())
                    );
                }

                PrexorCloudManager.serviceDriver.getService(service).handleCloudPlayerConnection(true);

                if (Driver.getInstance().getOfflinePlayerCacheDriver().readConfig().getPlayerCaches().stream().anyMatch(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())))) {
                    OfflinePlayerCacheConfiguration config = Driver.getInstance().getOfflinePlayerCacheDriver().readConfig();
                    OfflinePlayerCache offlinePlayerCache = config.getPlayerCaches().stream().filter(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName()))).findFirst().orElse(null);

                    assert offlinePlayerCache != null;

                    offlinePlayerCache.setLastConnected("NOW");
                    offlinePlayerCache.setUuid(UUIDDriver.getUUID(packetCast.getName()));
                    offlinePlayerCache.setConnectionCount(offlinePlayerCache.getConnectionCount() + 1);
                    offlinePlayerCache.setName(packetCast.getName());
                    offlinePlayerCache.setLastProxy(packetCast.getProxy());

                    config.getPlayerCaches().removeIf(c -> c.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())));
                    config.getPlayerCaches().add(offlinePlayerCache);

                    Driver.getInstance().getOfflinePlayerCacheDriver().saveConfig(config);
                } else {
                    OfflinePlayerCacheConfiguration config = Driver.getInstance().getOfflinePlayerCacheDriver().readConfig();
                    OfflinePlayerCache cache1 = new OfflinePlayerCache(
                            packetCast.getName(),
                            String.valueOf(System.currentTimeMillis()),
                            "NOW",
                            packetCast.getProxy(),
                            "",
                            UUIDDriver.getUUID(packetCast.getName()),
                            1,
                            0
                    );

                    config.getPlayerCaches().add(cache1);
                    Driver.getInstance().getOfflinePlayerCacheDriver().saveConfig(config);
                }
            }
        } else if (packet instanceof PacketInPlayerDisconnect packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutPlayerDisconnect(packetCast.getName()));
            CloudPlayerRestCache restCech = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName())), CloudPlayerRestCache.class);

            if (PrexorCloudManager.serviceDriver.getService(restCech.getProxy()) != null) {
                if (PrexorCloudManager.serviceDriver.getService(restCech.getProxy()).getEntry().getServiceState() != ServiceState.QUEUED)
                    PrexorCloudManager.serviceDriver.getService(restCech.getProxy()).handleCloudPlayerConnection(false);
            }

            if (!restCech.getService().equalsIgnoreCase("")) {
                if (PrexorCloudManager.serviceDriver.getService(restCech.getService()) != null) {
                    if (PrexorCloudManager.serviceDriver.getService(restCech.getService()).getEntry().getServiceState() != ServiceState.QUEUED)
                        PrexorCloudManager.serviceDriver.getService(restCech.getService()).handleCloudPlayerConnection(false);
                }
            }

            PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(WebServer.Routes.PLAYER_GENERAL.getRoute()), PlayerGeneral.class);
            general.getPlayers().removeIf(s -> s.equalsIgnoreCase(UUIDDriver.getUUID(packetCast.getName()).toString()));

            Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.PLAYER_GENERAL.getRoute(), new ConfigDriver().convert(general));
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudPlayerDisconnectedEvent(packetCast.getName(), UUIDDriver.getUUID(packetCast.getName())));

            Driver.getInstance().getWebServer().removeRoute("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName()));
            Driver.getInstance().getWebServer().removeRoute("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName()));

            if (PrexorCloudManager.config.isShowConnectingPlayers()) {
                Driver.getInstance().getTerminalDriver().log(
                        Type.NETWORK,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-player-disconnect")
                                .replace("%player%", packetCast.getName())
                                .replace("%uuid%", Objects.requireNonNull(UUIDDriver.getUUID(packetCast.getName()).toString()))
                );
            }

            if (Driver.getInstance().getOfflinePlayerCacheDriver().readConfig().getPlayerCaches().stream().anyMatch(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())))) {
                OfflinePlayerCacheConfiguration config = Driver.getInstance().getOfflinePlayerCacheDriver().readConfig();
                OfflinePlayerCache offlinePlayerCache = config.getPlayerCaches().stream().filter(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName()))).findFirst().orElse(null);

                assert offlinePlayerCache != null;

                offlinePlayerCache.setLastConnected(String.valueOf(System.currentTimeMillis()));
                config.getPlayerCaches().removeIf(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())));
                config.getPlayerCaches().add(offlinePlayerCache);

                Driver.getInstance().getOfflinePlayerCacheDriver().saveConfig(config);
            }
        } else if (packet instanceof PacketInPlayerSwitchService packetCast) {
            CloudPlayerRestCache restCech = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName())), CloudPlayerRestCache.class);

            if (PrexorCloudManager.config.isShowConnectingPlayers()) {
                Driver.getInstance().getTerminalDriver().log(
                        Type.NETWORK,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-player-switch-server")
                                .replace("%player%", packetCast.getName())
                                .replace("%uuid%", Objects.requireNonNull(Objects.requireNonNull(UUIDDriver.getUUID(packetCast.getName())).toString()))
                                .replace("%service%", packetCast.getServer())
                );
            }

            if (!restCech.getService().equalsIgnoreCase("")) {
                if (PrexorCloudManager.serviceDriver.getService(restCech.getService()) != null) {
                    if (PrexorCloudManager.serviceDriver.getService(restCech.getService()).getEntry().getServiceState() != ServiceState.QUEUED)
                        PrexorCloudManager.serviceDriver.getService(restCech.getService()).handleCloudPlayerConnection(false);
                }
            }

            String from = restCech.getService();
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutPlayerSwitchService(packetCast.getName(), packetCast.getServer(), from));
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudPlayerSwitchEvent(packetCast.getName(), restCech.getService(), packetCast.getServer(), UUIDDriver.getUUID(packetCast.getName())));
            PrexorCloudManager.serviceDriver.getService(packetCast.getServer()).handleCloudPlayerConnection(true);
            restCech.setService(packetCast.getServer());
            Driver.getInstance().getWebServer().updateRoute("/cloudplayer/" + UUIDDriver.getUUID(packetCast.getName()), (new RestDriver()).convert(restCech));

            if (Driver.getInstance().getOfflinePlayerCacheDriver().readConfig().getPlayerCaches().stream().anyMatch(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())))) {
                OfflinePlayerCacheConfiguration config = Driver.getInstance().getOfflinePlayerCacheDriver().readConfig();
                OfflinePlayerCache offlinePlayerCache = config.getPlayerCaches().stream().filter(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName()))).findFirst().orElse(null);

                assert offlinePlayerCache != null;

                offlinePlayerCache.setLastService(packetCast.getServer());
                offlinePlayerCache.setServerSwitchCount(offlinePlayerCache.getServerSwitchCount() + 1);

                config.getPlayerCaches().removeIf(playerCache -> playerCache.getUuid().equals(UUIDDriver.getUUID(packetCast.getName())));
                config.getPlayerCaches().add(offlinePlayerCache);

                Driver.getInstance().getOfflinePlayerCacheDriver().saveConfig(config);
            }
        } else if (packet instanceof PacketInCloudPlayerComponent packetCast) {
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutCloudPlayerComponent(packetCast.getComponent(), packetCast.getPlayer()));
        }
    }
}
