package org.mangorage.classloader.util;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.internal.PluginClassloader;

public abstract class JavaPlugin implements IPlugin {
    public JavaPlugin() {
        if (!(super.getClass().getClassLoader() instanceof PluginClassloader)) {
            throw new IllegalStateException("Not loaded by correct classloader");
        }
    }
}
