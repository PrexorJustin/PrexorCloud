package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class NetworkEntry {

    private Integer globalPlayers, globalPlayersPotency;
    private HashMap<String, Integer> groupPlayerPotency;

    public NetworkEntry() {
        this.globalPlayers = 0;
        this.globalPlayersPotency = 0;
        this.groupPlayerPotency = new HashMap<>();
    }

}
