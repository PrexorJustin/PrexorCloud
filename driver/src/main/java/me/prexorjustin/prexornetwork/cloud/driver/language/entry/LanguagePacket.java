package me.prexorjustin.prexornetwork.cloud.driver.language.entry;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class LanguagePacket {

    private final HashMap<String, String> data;

    public LanguagePacket() {
        this.data = new HashMap<>();
    }

    public String getMessage(String key) {
        return this.data.get(key);
    }

    public void update(HashMap<String, String> map) {
        this.data.clear();
        this.data.putAll(map);
    }

    public void add(String key, String value) {
        this.data.put(key, value);
    }
}
