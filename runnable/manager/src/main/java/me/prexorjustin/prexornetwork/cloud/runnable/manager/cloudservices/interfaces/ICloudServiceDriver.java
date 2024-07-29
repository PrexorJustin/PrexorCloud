package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.interfaces;

import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedEntry;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedService;

import java.util.ArrayDeque;
import java.util.List;

public interface ICloudServiceDriver {

    TaskedService register(TaskedEntry entry);

    void unregister(String service);

    void unregistered(String service);

    Integer getFreeUUID(String group);

    String getFreeUUID();

    Integer getActiveServices(String group);

    Integer getLobbiedServices(String group);

    Integer getFreePort(boolean proxy);

    void handleServices();

    TaskedService getService(String service);

    ArrayDeque<TaskedService> getServices();

    List<TaskedService> getServices(String group);

    List<TaskedService> getServicesFromNode(String node);

}
