package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.event.Event;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginManagerInternal {
    private static final Map<String, PluginContainerImpl> plugins = new ConcurrentHashMap<>();
    private static final ClassLoader parent = Thread.currentThread().getContextClassLoader().getParent();

    public static void unloadPlugin(Path path, PluginInfo pluginInfo) {
        try {
            plugins.put(
                    pluginInfo.pluginId(),
                    new PluginContainerImpl(
                            new PluginMetadataImpl(
                                    pluginInfo.mainClass(),
                                    pluginInfo.pluginId(),
                                    path.toUri().toURL()
                            )
                    )
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unloadPlugin(String id) {
        var container = plugins.get(id);
        if (container == null) return;
        disablePlugin(container);
        plugins.remove(id);
    }

    public static void enableAll() {
        plugins.values().forEach(PluginManagerInternal::enablePlugin);
    }

    public static void disableAll() {
        plugins.values().forEach(PluginManagerInternal::disablePlugin);
    }

    public static void enablePlugin(PluginContainerImpl container) {
        if (container.status == PluginStatus.ENABLED) return;
        try (var cl = new PluginClassloader(container, parent)) {
            Thread.currentThread().setContextClassLoader(cl);
            var plugin = cl.loadPlugin();
            container.setActiveState(plugin, cl);
            plugin.onLoad();
            Thread.currentThread().setContextClassLoader(parent);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disablePlugin(PluginContainerImpl container) {
        if (container.status == PluginStatus.DISABLED) return;
        plugins.values().forEach(PluginContainerImpl::disable);
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
