package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayDeque;

@Getter
@Setter
@NoArgsConstructor
public class LiveServiceList implements IConfigAdapter {

    private String cloudServiceSplitter;
    private ArrayDeque<String> cloudServices;

    public void remove(String cloudService) {
        this.cloudServices.removeIf(s -> s.equalsIgnoreCase(cloudService));
    }
}
