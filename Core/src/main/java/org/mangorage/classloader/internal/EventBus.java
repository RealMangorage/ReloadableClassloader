package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.event.IEventBus;
import org.mangorage.classloader.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventBus implements IEventBus {
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> map = new HashMap<>();

    @Override
    public <E extends Event> void addListener(Class<E> eClass, Consumer<E> event) {
        map.computeIfAbsent(eClass, a -> new CopyOnWriteArrayList<>()).add(event);
    }

    @Override
    public <E extends Event> void post(E event) {
        List<Consumer<E>> list = cast(map.get(event.getClass()));
        if (list != null)
            list.forEach(c -> c.accept(event));
    }

    private <T> T cast(Object o) {
        return (T) o;
    }
}
