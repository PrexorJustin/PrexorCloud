package me.prexorjustin.prexornetwork.cloud.runnable.manager;

import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.storage.IRunAble;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;

import java.util.Timer;

public class PrexorCloudManager implements IRunAble {

    public static RestDriver restDriver;
    public static ManagerConfig config;
    public static boolean shutdown;
    private static Timer timer;

    @Override
    public void run() {

    }
}
