package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

@UtilityClass
public class ServerDriverUtil {

    public boolean serverExists(String serverName) {
        return ProxyServer.getInstance().getServerInfo(serverName) != null;
    }

    public void addServer(ServerInfo serverInfo) {
        if (serverExists(serverInfo.getName())) return;

        ProxyServer.getInstance().getServers().put(serverInfo.getName(), serverInfo);
        ServerConfigUtil.addServerToConfig(serverInfo);
    }

    public void removeServer(String serverName) {
        if (!serverExists(serverName)) return;

        ProxyServer.getInstance().getServers().remove(serverName);
        ServerConfigUtil.removeServerFromConfig(serverName);
    }
}
