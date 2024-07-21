package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.interfaces;

import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;

public interface ITaskedService {

    void handleExecute(String line);

    void handleSync();

    void handleLaunch();

    void handleScreen();

    void handleQuit();

    void handleRestart();

    void handleStatusChange(ServiceState status);

    void handleCloudPlayerConnection(boolean connect);
}
