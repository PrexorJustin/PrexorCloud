package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.migrate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class MigrateOfflinePlayerCacheConfiguration implements IConfigAdapter {

    private ArrayList<MigrateOfflinePlayer> playerCaches;

}
