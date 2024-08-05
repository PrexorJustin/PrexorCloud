package me.prexorjustin.prexornetwork.cloud.api.offlineplayer.async;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.async.entrys.AsyncOfflinePlayer;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCacheConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class AsyncOfflinePlayerPool {

    public CompletableFuture<ArrayList<AsyncOfflinePlayer>> getAllAsyncOfflinePlayers() {
        CompletableFuture.supplyAsync(() -> {
            ArrayList<AsyncOfflinePlayer> players = new ArrayList<>();

            OfflinePlayerCacheConfiguration configuration = (OfflinePlayerCacheConfiguration) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.PLAYER_OFFLINECACHE.getRoute()), OfflinePlayerCacheConfiguration.class);
            configuration.getPlayerCaches().forEach(cache -> players.add(new AsyncOfflinePlayer(
                    cache.getName(),
                    cache.getFirstConnected(),
                    cache.getLastConnected(),
                    cache.getLastProxy(),
                    cache.getLastService(),
                    cache.getUuid(),
                    cache.getConnectionCount(),
                    cache.getServerSwitchCount()))
            );
            return players;
        });
        return null;
    }
}
