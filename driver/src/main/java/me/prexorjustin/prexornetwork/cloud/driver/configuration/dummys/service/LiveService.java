package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@NoArgsConstructor
public class LiveService implements IConfigAdapter {

    private String service, group, managerAddress, runningNode;
    private Integer port, restPort, networkingPort;

}
