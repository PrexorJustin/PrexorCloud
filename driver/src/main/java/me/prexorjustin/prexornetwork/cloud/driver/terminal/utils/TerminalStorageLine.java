package me.prexorjustin.prexornetwork.cloud.driver.terminal.utils;

import java.util.List;
import java.util.function.Consumer;

public record TerminalStorageLine(List<String> tabCompletes, Consumer<String> inputs) {

}
