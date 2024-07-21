package me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.interfaces;

public interface ICloudPlayerRestCache {

    void handleConnect(String proxyServiceName);

    void handleDisconnect();

    void handleSwitch(String switchedToService);
}
