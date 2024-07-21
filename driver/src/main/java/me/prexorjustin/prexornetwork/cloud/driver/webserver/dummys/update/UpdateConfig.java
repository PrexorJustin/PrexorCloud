package me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfig implements IConfigAdapter {

    private String data;

}
