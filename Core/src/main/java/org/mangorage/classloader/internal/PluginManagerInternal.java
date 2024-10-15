package org.mangorage.classloader.internal;


import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.event.Event;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginManagerInternal {
    private static final Object lock = new Object();
    private static final Map<String, PluginContainerImpl> plugins = new ConcurrentHashMap<>();
    private static final DependencyGraph graph = new DependencyGraph();

    public static final ClassLoader context = Thread.currentThread().getContextClassLoader();
    private static final ClassLoader parent = Thread.currentThread().getContextClassLoader().getParent();


    public static List<PluginContainerImpl> getSortedContainers() {
        return graph.computeContainerList(plugins);
    }

    public static Collection<PluginContainerImpl> getContainers() {
        return plugins.values();
    }

    public static boolean isLoaded(String id) {
        return plugins.containsKey(id);
    }

    public static void loadPlugin(Path path, PluginInfo pluginInfo) {
        synchronized (lock) {
            try {
                plugins.put(
                        pluginInfo.pluginId(),
                        new PluginContainerImpl(
                                new PluginMetadataImpl(
                                        pluginInfo.mainClass(),
                                        pluginInfo.pluginId(),
                                        path.toUri().toURL(),
                                        pluginInfo.depends() == null ? List.of() : pluginInfo.depends()
                                )
                        )
                );
                graph.addPlugin(pluginInfo.pluginId(), pluginInfo.depends());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void unloadPlugin(String id) {
        synchronized (lock) {
            var container = plugins.get(id);
            if (container == null) return;
            disablePlugin(container);
            plugins.remove(id);
            graph.removePlugin(id);
        }
    }

    public static void enableAll() {
        getSortedContainers().forEach(PluginManagerInternal::enablePlugin);
    }

    public static void disableAll() {
        getContainers().forEach(PluginManagerInternal::disablePlugin);
    }

    public static void enablePlugin(PluginContainerImpl container) {
        if (container.status == PluginStatus.ENABLED) return;
        try (var cl = new PluginClassloader(container, parent)) {
            Thread.currentThread().setContextClassLoader(cl);
            container.enable(cl);
            Thread.currentThread().setContextClassLoader(context);
        } catch (Throwable e) {
            System.out.println("Failed to enable Plugin %s, disabling".formatted(container.getMetadata().pluginId()));
            e.printStackTrace();
            container.disable();
        }
    }

    public static void disablePlugin(PluginContainerImpl container) {
        if (container.status == PluginStatus.DISABLED) return;
        try {
            container.disable();
        } catch (Throwable e) {
            System.out.println("Failed to disable plugin");
            e.printStackTrace();
        }
    }

    public static Optional<IPluginContainer> findContainer(IPlugin plugin) {
        for (IPluginContainer iPluginContainer : plugins.values()) {
            if (iPluginContainer.getPlugin() == plugin)
                return Optional.of(iPluginContainer);
        }

        return Optional.empty();
    }

    public static <E extends Event> void post(Event event) {
        plugins.values().forEach(pl -> {
            if (pl.status == PluginStatus.ENABLED)
                pl.getEventBus().post(event);
        });
    }
}
