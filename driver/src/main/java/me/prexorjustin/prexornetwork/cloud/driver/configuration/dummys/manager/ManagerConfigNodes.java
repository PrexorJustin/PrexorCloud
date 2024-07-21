package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@NoArgsConstructor
public class ManagerConfigNodes implements IConfigAdapter {

    private String name, address;

}
