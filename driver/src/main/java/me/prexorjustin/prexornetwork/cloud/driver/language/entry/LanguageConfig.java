package me.prexorjustin.prexornetwork.cloud.driver.language.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class LanguageConfig implements IConfigAdapter {

    private HashMap<String, String> messages;

}
