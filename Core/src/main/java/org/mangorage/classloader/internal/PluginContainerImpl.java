package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginClassloader;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.api.IPluginMetadata;
import org.mangorage.classloader.api.ITask;
import org.mangorage.classloader.api.event.IEventBus;
import org.mangorage.classloader.util.ReloadableValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PluginContainerImpl implements IPluginContainer {
    private final IPluginMetadata metadata;

    private final ReloadableValue<IEventBus> bus = ReloadableValue.of(EventBus::new);
    private final ReloadableValue<ExecutorService> executorService = ReloadableValue.of(Executors::newCachedThreadPool, s -> {
        s.shutdownNow();
        s.close();
    });

    private IPlugin plugin;
    private PluginClassloader classLoader;

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
        return bus.get();
    }

    @Override
    public IPluginClassloader getClassloader() {
        return classLoader;
    }

    @Override
    public void schedule(ITask task) {
        executorService.getOptional().ifPresent(s -> s.submit(task::run));
    }

    public void disable() {
        this.status = PluginStatus.DISABLED;
        this.plugin.unload();

        this.plugin = null;
        this.classLoader = null;

        this.executorService.unloadValue();
        this.bus.unloadValue();
    }

    public void setActiveState(IPlugin plugin, PluginClassloader classLoader) {
        this.plugin = plugin;
        this.classLoader = classLoader;

        this.bus.loadValue();
        this.executorService.loadValue();
        this.status = PluginStatus.ENABLED;
    }
}
