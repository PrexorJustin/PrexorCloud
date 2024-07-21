package me.prexorjustin.prexornetwork.cloud.driver.event;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.ICloudListener;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.IEventAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.event.entrys.Subscribe;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class EventDriver {

    public static final int PRE = -1;
    public static final int ALL = 0;
    public static final int POST = 1;

    private final Map<Class<? extends IEventAdapter>, Collection<EventProcess>> bindings;
    private final Set<ICloudListener> registeredListeners;

    public EventDriver() {
        this.bindings = new HashMap<>();
        this.registeredListeners = new HashSet<>();
    }

    public List<EventProcess> getProcessFor(Class<? extends IEventAdapter> clazz) {
        if (!this.bindings.containsKey(clazz)) return Collections.emptyList();
        return this.bindings.get(clazz).stream().toList();
    }

    public <T extends IEventAdapter> T executeEvent(T event, int i) {
        Collection<EventProcess> handlers = this.bindings.get(event.getClass());
        if (handlers == null) return event;

        for (EventProcess handler : handlers) {
            if (i == PRE && handler.getPriority() >= 0) continue;
            if (i == POST && handler.getPriority() < 0) continue;
            handler.execute(event);
        }

        return event;
    }

    public <T extends IEventAdapter> T executeEvent(T event) {
        return this.executeEvent(event, ALL);
    }

    public void registerListener(final ICloudListener listener) {
        if (this.registeredListeners.contains(listener)) return;
        this.registeredListeners.add(listener);

        Method[] methods = listener.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            if (annotation == null) continue;

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1) continue;

            Class<?> parameter = parameters[0];
            if (!method.getReturnType().equals(void.class)) continue;

            if (IEventAdapter.class.isAssignableFrom(parameter)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEventAdapter> realParameter = (Class<? extends IEventAdapter>) parameter;
                if (!this.bindings.containsKey(realParameter)) this.bindings.put(realParameter, new TreeSet<>());

                Collection<EventProcess> eventHandlerForEvent = this.bindings.get(realParameter);
                eventHandlerForEvent.add(createEventHandler(listener, method, annotation));
            }
        }
    }

    public void removeListener(ICloudListener listener) {
        this.bindings.forEach((key, value) -> value.removeIf(eventProcess -> eventProcess.listener() == listener));

        this.registeredListeners.remove(listener);
    }

    private EventProcess createEventHandler(final ICloudListener listener, final Method method, final Subscribe annotation) {
        return new EventProcess(listener, method, annotation);
    }

    public void clearListeners() {
        this.bindings.clear();
        this.registeredListeners.clear();
    }
}
