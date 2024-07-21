package me.prexorjustin.prexornetwork.cloud.api.player.sync;

import lombok.NonNull;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.player.sync.entrys.CloudPlayer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPool extends ICloudPlayerPool {

    public CloudPlayer getPlayer(String username) {
        return ((CloudPlayer) getConnectedPlayers().stream().filter(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null));
    }

    public CloudPlayer getPlayer(UUID uuid) {
        return ((CloudPlayer) this.getConnectedPlayers().stream().filter(cloudPlayer -> cloudPlayer.getUuid().equals(uuid)).findFirst().orElse(null));
    }

    public List<CloudPlayer> getAllPlayerFromService(String serviceName) {
        return this.getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getService() != null && cloudPlayer.getService().getName().equalsIgnoreCase(serviceName))
                .map(CloudPlayer.class::cast)
                .collect(Collectors.toList());
    }

    public List<CloudPlayer> getAllPlayerFromProxy(String proxyName) {
        return this.getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getName().equalsIgnoreCase(proxyName))
                .map(CloudPlayer.class::cast)
                .collect(Collectors.toList());
    }

    public List<CloudPlayer> getAllPlayerFromProxyGroup(String groupName) {
        return this.getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getGroup().getName().equalsIgnoreCase(groupName))
                .map(CloudPlayer.class::cast)
                .collect(Collectors.toList());
    }

    public List<CloudPlayer> getAllPlayerFromServiceGroup(String groupName) {
        return this.getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getService() != null && cloudPlayer.getService().getGroup().getName().equalsIgnoreCase(groupName))
                .map(CloudPlayer.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPlayerNull(@NonNull String username) {
        return getConnectedPlayers().stream().noneMatch(cloudPlayer -> cloudPlayer.getUsername().equals(username));
    }

    @Override
    public void registerPlayer(@NonNull ICloudPlayer player) {
        if (getConnectedPlayers().stream().noneMatch(cloudPlayer -> cloudPlayer.getUuid().equals(player.getUuid())))
            getConnectedPlayers().add(player);
    }

}
