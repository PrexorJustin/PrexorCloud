package me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.HashMap;

@Getter
@NoArgsConstructor
public class Messages implements IConfigAdapter {

    public HashMap<String, String> messages;

    public Messages(HashMap<String, String> messages) {
        this.messages = messages;
    }
}
