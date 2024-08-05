package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCache;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached.OfflinePlayerCacheConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.migrate.MigrateOfflinePlayerCacheConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;

import java.io.File;
import java.util.ArrayList;

public class OfflinePlayerCacheDriver {

    private final ConfigDriver configDriver;

    public OfflinePlayerCacheDriver() {
        this.configDriver = new ConfigDriver("./local/storage/cloudPlayer.storage");

        if (!this.configDriver.exists()) this.configDriver.save(new OfflinePlayerCacheConfiguration(new ArrayList<>()));
        else if (!this.configDriver.canBeRead(OfflinePlayerCacheConfiguration.class)) {
            MigrateOfflinePlayerCacheConfiguration migration = (MigrateOfflinePlayerCacheConfiguration) this.configDriver.read(MigrateOfflinePlayerCacheConfiguration.class);
            ArrayList<OfflinePlayerCache> offlinePlayerCaches = new ArrayList<>();
            migration.getPlayerCaches().forEach(migrateOfflinePlayer -> offlinePlayerCaches.add(new OfflinePlayerCache(
                    migrateOfflinePlayer.getName(),
                    migrateOfflinePlayer.getFirstConnected(),
                    migrateOfflinePlayer.getLastConnected(),
                    migrateOfflinePlayer.getLastProxy(),
                    migrateOfflinePlayer.getLastService(),
                    migrateOfflinePlayer.getUuid(),
                    migrateOfflinePlayer.getConnectionCount(),
                    migrateOfflinePlayer.getServerSwitchCount()
            )));

            new File("./local/storage/cloudPlayer.storage").deleteOnExit();

            this.configDriver.save(new OfflinePlayerCacheConfiguration(offlinePlayerCaches));
        }

        Driver.getInstance().getWebServer().addRoute(new RouteEntry(WebServer.Routes.PLAYER_OFFLINECACHE.getRoute(), this.configDriver.convert(readConfig())));
    }

    public OfflinePlayerCacheConfiguration readConfig() {
        return (OfflinePlayerCacheConfiguration) new ConfigDriver("./local/storage/cloudPlayer.storage").read(OfflinePlayerCacheConfiguration.class);
    }

    public void saveConfig(OfflinePlayerCacheConfiguration cacheConfiguration) {
        this.configDriver.save(cacheConfiguration);
        Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.PLAYER_OFFLINECACHE.getRoute(), this.configDriver.convert(cacheConfiguration));
    }
}
