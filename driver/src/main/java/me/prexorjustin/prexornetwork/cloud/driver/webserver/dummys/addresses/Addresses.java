package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.addresses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class Addresses implements IConfigAdapter {

    private ArrayList<String> addresses;

}
