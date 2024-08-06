package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;

@Data
@Builder
@Jacksonized
public class LiveServices implements IConfigAdapter {

    private String name, group, host, node;
    private int uuid, players, port;
    private ServiceState state;
    private long lastReaction;

}
