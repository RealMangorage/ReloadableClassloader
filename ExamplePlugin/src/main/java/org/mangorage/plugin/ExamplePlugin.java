package org.mangorage.plugin;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.PluginManager;
import org.mangorage.classloader.event.ExampleEvent;

public class ExamplePlugin implements IPlugin {
    private static final boolean useDirect = true;

    public ExamplePlugin() {
        System.out.println("LOL 28");
    }

    @Override
    public void onLoad() {
        PluginManager.register(this, ExampleEvent.class, e -> {
            //System.out.println("OBV");
        });

        PluginManager.schedule(this, () -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("LOL 23 -> " + this);
            }
        });
    }

    @Override
    public void unload() {
        PluginManager.remove("example");
    }
}
