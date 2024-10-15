package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginClassloader;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.api.IPluginMetadata;
import org.mangorage.classloader.api.ITask;
import org.mangorage.classloader.api.event.IEventBus;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PluginContainerImpl implements IPluginContainer {
    private final IPluginMetadata metadata;

    private IPlugin plugin;
    private PluginClassloader classLoader;
    private IEventBus bus;
    private ExecutorService executorService;

    PluginStatus status = PluginStatus.DISABLED;

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

    @Override
    public IPluginClassloader getClassloader() {
        return classLoader;
    }

    @Override
    public void schedule(ITask task) {
        executorService.submit(task::run);
    }

    public void disable() {
        this.status = PluginStatus.DISABLED;
        plugin.unload();
        this.executorService.shutdownNow();
        this.executorService.close();
    }

    public void setActiveState(IPlugin plugin, PluginClassloader classLoader) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.bus = new EventBus();
        this.executorService = Executors.newCachedThreadPool();
        this.status = PluginStatus.ENABLED;
    }
}
