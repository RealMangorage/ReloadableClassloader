package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.event.Event;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PluginManagerInternal {
    enum State {
        LOADED,
        UNLOADED;
    }

    private static final List<IPluginContainer> plugins = new ArrayList<>();
    private static final ClassLoader parent = Thread.currentThread().getContextClassLoader().getParent();
    private static State activeState = State.UNLOADED;

    public static void addPlugin(Path path, String mainClass) {
        try {
            plugins.add(
                    new PluginContainerImpl(
                            new PluginMetadataImpl(
                                    mainClass,
                                    path.toUri().toURL()
                            )
                    )
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        if (activeState != State.UNLOADED) return;
        plugins.forEach(pl -> {
            try {
                try (var cl = new PluginClassloader(parent)) {
                    Thread.currentThread().setContextClassLoader(cl);
                    var plugin = cl.loadPlugin(pl);
                    ((PluginContainerImpl) pl).setActiveState(plugin, cl);
                    plugin.onLoad();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread.currentThread().setContextClassLoader(parent);
        activeState = State.LOADED;
    }

    public static void unload() {
        if (activeState != State.LOADED) return;
        plugins.forEach(pl -> pl.getPlugin().unload());
        activeState = State.UNLOADED;
    }

    public static Optional<IPluginContainer> findContainer(IPlugin plugin) {
        for (IPluginContainer iPluginContainer : plugins) {
            if (iPluginContainer.getPlugin() == plugin)
                return Optional.of(iPluginContainer);
        }
        return Optional.empty();
    }

    public static <E extends Event> void post(Event event) {
        plugins.forEach(pl -> pl.getEventBus().post(event));
    }
}
