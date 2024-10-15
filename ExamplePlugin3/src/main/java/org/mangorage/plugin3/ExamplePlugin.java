package org.mangorage.plugin3;

import org.mangorage.classloader.api.IPlugin;

public class ExamplePlugin implements IPlugin {

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
