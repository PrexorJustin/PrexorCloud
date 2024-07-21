package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry;

import lombok.Getter;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;

@Getter
@Setter
public class TaskedEntry {

    private final String groupName, serviceName, taskNode, uuid;
    private final int useId;
    private String customTemplate;
    private int currentPlayers, checkInterval, checkIntervalPlayers, usedPort;
    private boolean useProtocol, useCustomTemplate;
    private long time;
    private ServiceState serviceState;

    public TaskedEntry(int used_port, String group_name, String service_name, String task_node, boolean use_protocol, String usedId, boolean useCustomTemplate, String customTemplate) {
        this.currentPlayers = 0;
        this.checkInterval = 0;
        this.checkIntervalPlayers = 0;
        this.time = System.currentTimeMillis();
        this.usedPort = used_port;
        this.groupName = group_name;
        this.serviceName = service_name;
        this.taskNode = task_node;
        this.serviceState = ServiceState.QUEUED;
        this.useProtocol = use_protocol;
        this.uuid = java.util.UUID.randomUUID().toString();
        this.useId = Integer.parseInt(usedId);
        this.useCustomTemplate = useCustomTemplate;
        this.customTemplate = customTemplate;
    }

}
