package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.interfaces.ICloudPlayerRestCache;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.interfaces.IRest;

@NoArgsConstructor
@Getter
public class CloudPlayerRestCache implements ICloudPlayerRestCache, IConfigAdapter, IRest {

    private String name, uuid;

    @Setter
    private String proxy, service;
    private Long connectTime;

    public CloudPlayerRestCache(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
        connectTime = System.currentTimeMillis();
    }

    @Override
    public void handleConnect(String proxyServiceName) {
        this.proxy = proxyServiceName;
    }

    @Override
    public void handleDisconnect() {

    }

    @Override
    public void handleSwitch(String switchedToService) {
        this.service = switchedToService;
    }
}
