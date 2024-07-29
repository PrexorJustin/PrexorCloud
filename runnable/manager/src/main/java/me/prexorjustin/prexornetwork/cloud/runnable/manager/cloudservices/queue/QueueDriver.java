package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.queue;

import lombok.Getter;

import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
public class QueueDriver {

    private final ConcurrentLinkedDeque<String> startupQueue = new ConcurrentLinkedDeque<>(), shutdownQueue = new ConcurrentLinkedDeque<>();

    public void addQueuedObjectToStart(String service) {
        if (!this.startupQueue.contains(service)) this.startupQueue.add(service);
    }

    public void addQueuedObjectToShutdown(String service) {
        if (!shutdownQueue.contains(service)) this.shutdownQueue.add(service);
    }
}
