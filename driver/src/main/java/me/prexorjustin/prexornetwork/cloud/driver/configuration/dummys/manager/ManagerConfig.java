package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayList;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
public class ManagerConfig implements IConfigAdapter {

    private String managerAddress, language, splitter, uuid, bungeeVersion, spigotVersion;
    private boolean useProtocol, autoUpdate, showConnectingPlayers, copyLogs;
    private Integer processorUsage, serviceStartupCount, canUseMemory, bungeePort, spigotPort, networkingPort, restPort, timeoutCheck;

    private HashSet<String> whitelist;
    private ArrayList<ManagerConfigNodes> nodes;
}
