package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.IPluginClassloader;
import org.mangorage.classloader.api.IPluginContainer;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginClassloader extends URLClassLoader implements IPluginClassloader {
    private final List<IPluginContainer> plugins = new ArrayList<>();

    public PluginClassloader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    IPlugin loadPlugin(IPluginContainer iPlugin) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        addURL(iPlugin.getMetadata().pluginURL());
        var clz = Class.forName(iPlugin.getMetadata().mainClass(), false, this);
        if (!IPlugin.class.isAssignableFrom(clz))
            throw new IllegalStateException("Main class of Plugin must implement org.mangorage.classloader.api.IPlugin");
        return (IPlugin) clz.newInstance();
    }

    @Override
    public IPluginContainer[] getPlugins() {
        return plugins.toArray(IPluginContainer[]::new);
    }
}
