package me.prexorjustin.prexornetwork.cloud.api.offlineplayer.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class ICloudOfflinePlayer {

    private final String name, firstConnected, lastConnected, lastProxy, lastService;
    private final UUID uuid;
    private final int connectionCount, serverSwitchCount;

}
