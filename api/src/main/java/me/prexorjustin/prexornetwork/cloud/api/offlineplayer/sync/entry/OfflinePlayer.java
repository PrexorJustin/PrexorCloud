package me.prexorjustin.prexornetwork.cloud.api.offlineplayer.sync.entry;

import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.interfaces.ICloudOfflinePlayer;

import java.util.UUID;

public class OfflinePlayer extends ICloudOfflinePlayer {

    public OfflinePlayer(String name, String firstConnected, String lastConnected, String lastProxy, String lastService, UUID uuid, int connectionCount, int serverSwitchCount) {
        super(name, firstConnected, lastConnected, lastProxy, lastService, uuid, connectionCount, serverSwitchCount);
    }

}
