package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class SoftwareConfig implements IConfigAdapter {

    private HashMap<String, String> spigots, proxies;

}
