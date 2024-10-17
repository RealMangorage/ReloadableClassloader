package org.mangorage.plugin3;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.util.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    public ExamplePlugin() {
        System.out.println("Plugin 3");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void unload() {

    }
}
