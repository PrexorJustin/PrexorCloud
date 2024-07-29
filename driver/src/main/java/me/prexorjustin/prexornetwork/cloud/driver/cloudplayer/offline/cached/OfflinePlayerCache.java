package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.cached;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfflinePlayerCache implements IConfigAdapter {

    private String name, firstConnected, lastConnected, lastProxy, lastService;
    private UUID uuid;
    private int connectionCount, serverSwitchCount;

}
