package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class OfflinePlayerCacheConfiguration implements IConfigAdapter {

    private ArrayList<OfflinePlayerCache> playerCaches;

}
