package org.mangorage.classloader.api;

import org.mangorage.classloader.event.Event;
import org.mangorage.classloader.internal.PluginManagerInternal;

import java.util.function.Consumer;

public final class PluginManager {

    public static <E extends Event> void register(IPlugin plugin, Class<E> eClass, Consumer<E> eConsumer) {
        var container = PluginManagerInternal.findContainer(plugin);
        if (container.isEmpty()) return;
        container.get().getEventBus().addListener(eClass, eConsumer);
    }

    public static void schedule(IPlugin plugin, ITask task) {
        var container = PluginManagerInternal.findContainer(plugin);
        if (container.isEmpty()) return;
        container.get().schedule(task);
    }

    public static void unloadPlugin(String id) {
        PluginManagerInternal.unloadPlugin(id);
    }

    public static boolean isPluginLoaded(String id) {
        return PluginManagerInternal.isLoaded(id);
    }
}
