package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class PlayerGeneral implements IConfigAdapter {

    private ArrayList<String> players;

}
