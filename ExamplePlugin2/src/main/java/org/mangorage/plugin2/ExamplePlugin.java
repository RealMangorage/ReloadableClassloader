package org.mangorage.plugin2;

import org.mangorage.classloader.util.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    public ExamplePlugin() {
        System.out.println("Plugin 2");
    }

    @Override
    public void onLoad() {
        Test.init();
    }

    @Override
    public void unload() {
    }
}
