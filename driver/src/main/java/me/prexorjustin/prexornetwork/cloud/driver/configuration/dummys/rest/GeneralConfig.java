package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.HashMap;

@Getter
@NoArgsConstructor
public class GeneralConfig implements IConfigAdapter {

    private final HashMap<String, String> config = new HashMap<>();

}
