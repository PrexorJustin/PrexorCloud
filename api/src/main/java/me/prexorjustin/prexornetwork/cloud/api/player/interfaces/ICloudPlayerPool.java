package me.prexorjustin.prexornetwork.cloud.api.player.interfaces;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.UUID;

@Getter
public abstract class ICloudPlayerPool {

    private final ArrayList<ICloudPlayer> connectedPlayers = new ArrayList<>();

    public abstract boolean isPlayerNull(@NonNull String username);

    public abstract void registerPlayer(@NonNull ICloudPlayer player);

    public void unregisterPlayer(@NonNull String username) {
        this.connectedPlayers.removeIf(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(username));
    }

    public boolean unregisterPlayer(@NonNull UUID uuid) {
        return this.connectedPlayers.removeIf(cloudPlayer -> cloudPlayer.getUuid().equals(uuid));
    }
}
