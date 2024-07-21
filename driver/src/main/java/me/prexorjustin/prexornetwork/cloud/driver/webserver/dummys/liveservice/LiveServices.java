package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;

@Getter
@Setter
@NoArgsConstructor
public class LiveServices implements IConfigAdapter {

    private String name, group, host, node;
    private int uuid, players, port;
    private ServiceState state;
    private long lastReaction;

}
