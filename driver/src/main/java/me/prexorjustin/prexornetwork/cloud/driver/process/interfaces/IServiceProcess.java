package me.prexorjustin.prexornetwork.cloud.driver.process.interfaces;

public interface IServiceProcess {

    void sync();

    void handleConsole();

    void launch();

    void restart();

    void shutdown();
}
