package org.mangorage.classloader.api;

import org.mangorage.classloader.api.event.IEventBus;
import org.mangorage.classloader.internal.PluginContainerImpl;

public sealed interface IPluginContainer permits PluginContainerImpl {
    IPlugin getPlugin();
    IPluginMetadata getMetadata();
    IEventBus getEventBus();
    void schedule(ITask runnable);
}
