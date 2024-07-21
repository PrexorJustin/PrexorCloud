package me.prexorjustin.prexornetwork.cloud.api.offlineplayer.async.entrys;

import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.interfaces.ICloudOfflinePlayer;

import java.util.UUID;

public class AsyncOfflinePlayer extends ICloudOfflinePlayer {

    public AsyncOfflinePlayer(String name, String firstConnected, String lastConnected, String lastProxy, String lastService, UUID uuid, int connectionCount, int serverSwitchCount) {
        super(name, firstConnected, lastConnected, lastProxy, lastService, uuid, connectionCount, serverSwitchCount);
    }

}
