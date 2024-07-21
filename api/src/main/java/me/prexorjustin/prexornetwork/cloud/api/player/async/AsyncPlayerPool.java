package me.prexorjustin.prexornetwork.cloud.api.player.async;

import lombok.NonNull;
import me.prexorjustin.prexornetwork.cloud.api.player.async.entrys.AsyncCloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayerPool;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncPlayerPool extends ICloudPlayerPool {

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayers() {
        return CompletableFuture.supplyAsync(() -> getConnectedPlayers().stream().map(AsyncCloudPlayer.class::cast).collect(Collectors.toList()));
    }

    public CompletableFuture<AsyncCloudPlayer> getPlayer(@NonNull String username) {
        return CompletableFuture.supplyAsync(() -> ((AsyncCloudPlayer) getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getUsername().equals(username)).findFirst().orElse(null))
        );
    }

    public CompletableFuture<AsyncCloudPlayer> getPlayer(@NonNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> ((AsyncCloudPlayer) getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getUuid().equals(uuid)).findFirst().orElse(null))
        );
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getAllPlayerFromService(String serviceName) {
        return CompletableFuture.supplyAsync(() -> getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getService() != null && cloudPlayer.getService().getName().equalsIgnoreCase(serviceName))
                .map(AsyncCloudPlayer.class::cast)
                .collect(Collectors.toList())
        );
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getAllPlayerFromProxy(@NonNull String Proxy) {
        return CompletableFuture.supplyAsync(() -> getConnectedPlayers().stream()
                .filter(cloudPlayer -> cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getName().equals(Proxy))
                .map(AsyncCloudPlayer.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean isPlayerNull(@NonNull String username) {
        return getConnectedPlayers().parallelStream().noneMatch(cloudPlayer -> cloudPlayer.getUsername().equals(username));
    }

    @Override
    public void registerPlayer(@NonNull ICloudPlayer player) {
        if (getConnectedPlayers().stream().noneMatch(cloudPlayer -> cloudPlayer.getUuid().equals(player.getUuid())))
            getConnectedPlayers().add(player);
    }
}
