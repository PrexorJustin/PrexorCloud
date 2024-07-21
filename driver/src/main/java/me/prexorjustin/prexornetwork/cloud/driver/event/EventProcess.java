package me.prexorjustin.prexornetwork.cloud.driver.event;

import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.ICloudListener;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public record EventProcess(ICloudListener listener, Method method,
                           Subscribe annotation) implements Comparable<EventProcess> {

    public void execute(IEventAdapter event) {
        if (annotation.async()) {
            CompletableFuture.runAsync(() -> {
                try {
                    method.invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            return;
        }

        try {
            method.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPriority() {
        return this.annotation.priority();
    }

    @Override
    public String toString() {
        return "(EventHandler " + this.listener + ": " + this.method.getName() + ")";
    }

    @Override
    public int compareTo(EventProcess other) {
        int annotation = this.annotation.priority() - other.annotation.priority();
        if (annotation == 0) annotation = this.listener.hashCode() - other.listener.hashCode();

        return annotation == 0 ? this.hashCode() - other.hashCode() : annotation;
    }
}
