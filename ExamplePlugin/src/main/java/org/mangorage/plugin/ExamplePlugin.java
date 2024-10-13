package org.mangorage.plugin;

import org.mangorage.classloader.api.IPlugin;
import org.mangorage.classloader.api.PluginManager;
import org.mangorage.classloader.event.ExampleEvent;

public class ExamplePlugin implements IPlugin {

    public ExamplePlugin() {
        System.out.println("LOL 28");
    }

    @Override
    public void onLoad() {
        PluginManager.register(this, ExampleEvent.class, e -> {
            //System.out.println("OBV");
        });

        if (!PluginManager.load)
            System.out.println("SCHEDULED");
            PluginManager.schedule(this, (s) -> {
                while (s.isRunning()) {
                    System.out.println(s.isRunning());
                    System.out.println(s.getId());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("LOL");
                }
            });
        PluginManager.load = true;
    }

    @Override
    public void unload() {

    }
}
