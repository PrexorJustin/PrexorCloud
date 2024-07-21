package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.authentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatorKey implements IConfigAdapter {

    private String key;

}
