package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Whitelist implements IConfigAdapter {

    private Set<String> whitelist;

}
