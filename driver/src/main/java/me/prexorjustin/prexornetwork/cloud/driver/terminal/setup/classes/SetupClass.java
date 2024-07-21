package me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.classes;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;

@Getter
public abstract class SetupClass {

    private final HashMap<String, Object> answers = new HashMap<>();
    private Integer step = 0;

    public abstract void call(String line);

    public abstract List<String> tabComplete();

    public void addStep() {
        this.step++;
    }
}
