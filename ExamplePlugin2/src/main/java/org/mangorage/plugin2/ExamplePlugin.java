package org.mangorage.plugin2;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.PluginManager;

public class ExamplePlugin implements IPlugin {
    public ExamplePlugin() {
        System.out.println("Plugin 2");
    }

    @Override
    public void onLoad() {
        if (PluginManager.isPluginLoaded("example"))
            Test.init();
    }

    @Override
    public void unload() {

    }
}
