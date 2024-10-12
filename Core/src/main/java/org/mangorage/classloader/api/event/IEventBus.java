package org.mangorage.classloader.api.event;

import org.mangorage.classloader.event.Event;
import org.mangorage.classloader.internal.EventBus;

import java.util.function.Consumer;

public sealed interface IEventBus permits EventBus {
    <E extends Event> void addListener(Class<E> eClass, Consumer<E> event);
    <E extends Event> void post(E event);
}
