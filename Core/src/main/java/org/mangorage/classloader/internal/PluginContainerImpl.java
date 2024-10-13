package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.api.IPluginMetadata;
import org.mangorage.classloader.api.ITask;
import org.mangorage.classloader.api.ITaskStatus;
import org.mangorage.classloader.api.event.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class PluginContainerImpl implements IPluginContainer {
    record TaskHolder(ITask iTask, AtomicBoolean status, UUID uuid) implements ITaskStatus {
        @Override
        public boolean isRunning() {
            return status.get();
        }

        @Override
        public UUID getId() {
            return uuid;
        }
    }

    private final IPluginMetadata metadata;

    private IPlugin plugin;
    private ClassLoader classLoader;
    private IEventBus bus;
    private ExecutorService executorService;
    private final List<TaskHolder> taskHolders = new ArrayList<>();

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
    public void schedule(ITask task) {
        var holder = new TaskHolder(task, new AtomicBoolean(true), UUID.randomUUID());
        taskHolders.add(holder);
        executorService.submit(() -> task.run(holder));
    }

    public void disable() {
        this.executorService.shutdownNow();
        this.executorService.close();
        this.taskHolders.forEach(holder -> holder.status().set(false));
        this.taskHolders.clear();
    }

    public void setActiveState(IPlugin plugin, ClassLoader classLoader) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.bus = new EventBus();
        this.executorService = Executors.newCachedThreadPool();
    }
}
