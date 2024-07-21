package me.prexorjustin.prexornetwork.cloud.driver.event.events.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@AllArgsConstructor
public class CloudServiceLaunchEvent extends IEventAdapter {

    private final String name, group, node;

}
