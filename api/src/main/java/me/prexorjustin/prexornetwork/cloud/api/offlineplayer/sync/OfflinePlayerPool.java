package me.prexorjustin.prexornetwork.cloud.api.offlineplayer.sync;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.sync.entry.OfflinePlayer;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCacheConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;

import java.util.ArrayList;

public class OfflinePlayerPool {

    public ArrayList<OfflinePlayer> getAllOfflinePlayers() {
        ArrayList<OfflinePlayer> players = new ArrayList<>();

        final OfflinePlayerCacheConfiguration configuration = (OfflinePlayerCacheConfiguration) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.PLAYER_OFFLINECACHE.getRoute()), OfflinePlayerCacheConfiguration.class);
        configuration.getPlayerCaches().forEach(cache -> players.add(new OfflinePlayer(
                cache.getName(),
                cache.getFirstConnected(),
                cache.getLastConnected(),
                cache.getLastProxy(),
                cache.getLastService(),
                cache.getUuid(),
                cache.getConnectionCount(),
                cache.getServerSwitchCount()
        )));
        return players;
    }

}
