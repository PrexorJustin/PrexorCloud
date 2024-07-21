package me.prexorjustin.prexornetwork.cloud.driver.event.events.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

@Getter
@AllArgsConstructor
public class CloudGroupDeleteEvent extends IEventAdapter {

    private final String groupName;

}
