package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayDeque;

@Getter
@Setter
@NoArgsConstructor
public class GroupList implements IConfigAdapter {

    private ArrayDeque<String> groups;

}
