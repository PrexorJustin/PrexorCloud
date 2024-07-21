package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@NoArgsConstructor
public class NodeConfig implements IConfigAdapter {

    private String language, managerAddress, bungeeVersion, spigotVersion, nodeName, nodeAddress;
    private Integer processorUsage, canUseMemory, bungeePort, spigotPort, networkingPort, restPort;
    private boolean autoUpdate, copyLogs;

}
