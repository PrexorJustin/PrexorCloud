package me.prexorjustin.prexornetwork.cloud.driver.event.events.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@RequiredArgsConstructor
public class CloudServiceConnectedEvent extends IEventAdapter {

    private final String name, node, host, group;
    private final int port;

}
