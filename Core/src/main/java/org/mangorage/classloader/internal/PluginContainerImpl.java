package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.api.IPluginMetadata;
import org.mangorage.classloader.api.event.IEventBus;

public final class PluginContainerImpl implements IPluginContainer {
    private final IPluginMetadata metadata;
    private IPlugin plugin;
    private ClassLoader classLoader;
    private IEventBus bus;

    public PluginContainerImpl(IPluginMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public IPlugin getPlugin() {
        return plugin;
    }

    @Override
    public IPluginMetadata getMetadata() {
        return metadata;
    }

    @Override
    public IEventBus getEventBus() {
        return bus;
    }

    public void setActiveState(IPlugin plugin, ClassLoader classLoader) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.bus = new EventBus();
    }
}
