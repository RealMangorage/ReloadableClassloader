package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginClassloader;
import org.mangorage.classloader.api.IPluginContainer;
import org.mangorage.classloader.util.JavaPlugin;

import java.net.URL;
import java.net.URLClassLoader;

public final class PluginClassloader extends URLClassLoader implements IPluginClassloader {
    static {
        if(!ClassLoader.registerAsParallelCapable())
            throw new IllegalStateException("Failed to register PluginClassloader as Parallel");
    }

    private final IPluginContainer plugin;

    public PluginClassloader(IPluginContainer plugin, ClassLoader parent) {
        super(new URL[]{}, parent);
        this.plugin = plugin;
        addURL(plugin.getMetadata().pluginURL());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> located = null;
        try {
            located = super.loadClass(name, resolve);
            var a = 1;
        } catch (Throwable throwable) {
            for (PluginContainerImpl container : PluginManagerInternal.getContainers()) {
                if (container == plugin) continue;
                try {
                    located = container.getClassloaderInternal().loadInternalClass(name, resolve);
                    if (located != null)
                        break;
                } catch (Throwable ignored) {}
            }
        }

        if (located != null)
            return located;
        throw new ClassNotFoundException("Could not find class %s".formatted(name));
    }

    Class<?> loadInternalClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }


    IPlugin loadPlugin() throws Throwable {
        var clz = Class.forName(plugin.getMetadata().mainClass(), false, this);
        if (!JavaPlugin.class.isAssignableFrom(clz))
            throw new IllegalStateException("Main class of Plugin must implement org.mangorage.classloader.api.IPlugin");
        return (IPlugin) clz.newInstance();
    }

    @Override
    public IPluginContainer getPlugin() {
        return plugin;
    }
}
