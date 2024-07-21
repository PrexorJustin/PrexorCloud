package me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@AllArgsConstructor
public class CloudRestAPIDeleteEvent extends IEventAdapter {

    private final String path;

}
