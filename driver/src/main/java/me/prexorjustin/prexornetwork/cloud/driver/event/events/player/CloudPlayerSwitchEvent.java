package me.prexorjustin.prexornetwork.cloud.driver.event.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CloudPlayerSwitchEvent extends IEventAdapter {

    private final String name, from, to;
    private final UUID UniqueId;

}
