package org.mangorage.plugin;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.PluginManager;
import org.mangorage.classloader.event.ExampleEvent;

import java.security.SecureRandom;

public class ExamplePlugin implements IPlugin {

    public ExamplePlugin() {
        System.out.println("LOL 28");
    }

    @Override
    public void onLoad() {
        PluginManager.register(this, ExampleEvent.class, e -> {
            //System.out.println("OBV");
        });
    }

    @Override
    public void unload() {

    }
}
