package me.prexorjustin.prexornetwork.cloud.driver.group.dummys;

import lombok.*;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group implements IConfigAdapter {

    @Setter(AccessLevel.NONE)
    private String name, groupType;
    @Setter(AccessLevel.NONE)
    private Integer usedMemory;
    @Setter(AccessLevel.NONE)
    private boolean maintenance, runStatic;

    private Integer priority, startPriority, maxPlayer, minOnline, maxOnline, startNewPercentage, over100AtGroup, over100AtNetwork;
    private String permission;

    private GroupStorage storage;

}
