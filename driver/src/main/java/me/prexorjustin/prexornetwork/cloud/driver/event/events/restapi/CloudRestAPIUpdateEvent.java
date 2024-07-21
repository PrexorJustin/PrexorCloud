package me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@AllArgsConstructor
public class CloudRestAPIUpdateEvent extends IEventAdapter {

    private final String path;
    private final String content;

}
