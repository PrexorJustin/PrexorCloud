package me.prexorjustin.prexornetwork.cloud.driver.event.events.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@AllArgsConstructor
public class CloudProxyConnectedEvent extends IEventAdapter {

    private final String name, node, host, group;
    private final Integer port;

}
