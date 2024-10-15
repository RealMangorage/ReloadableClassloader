package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginClassloader;
import org.mangorage.classloader.api.IPluginContainer;

import java.net.URL;
import java.net.URLClassLoader;

public final class PluginClassloader extends URLClassLoader implements IPluginClassloader {
    private final IPluginContainer plugin;

    public PluginClassloader(IPluginContainer plugin, ClassLoader parent) {
        super(new URL[]{}, parent);
        this.plugin = plugin;
        addURL(plugin.getMetadata().pluginURL());
    }

    IPlugin loadPlugin() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        var clz = Class.forName(plugin.getMetadata().mainClass(), false, this);
        if (!IPlugin.class.isAssignableFrom(clz))
            throw new IllegalStateException("Main class of Plugin must implement org.mangorage.classloader.api.IPlugin");
        return (IPlugin) clz.newInstance();
    }

    @Override
    public IPluginContainer getPlugin() {
        return plugin;
    }
}
