package me.prexorjustin.prexornetwork.cloud.driver.group.dummys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group implements IConfigAdapter {

    private String name, groupType;
    private Integer usedMemory;
    private boolean maintenance, runStatic;
    private Integer priority, startPriority, maxPlayer, minOnline, maxOnline, startNewPercentage, over100AtGroup, over100AtNetwork;
    private String permission;

    private GroupStorage storage;

}
