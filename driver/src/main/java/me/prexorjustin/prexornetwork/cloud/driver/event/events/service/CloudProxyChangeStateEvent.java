package me.prexorjustin.prexornetwork.cloud.driver.event.events.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@AllArgsConstructor
@Getter
public class CloudProxyChangeStateEvent extends IEventAdapter {

    private final String name, state;

}
