package org.mangorage.classloader;

import org.mangorage.classloader.event.ExampleEvent;
import org.mangorage.classloader.internal.PluginManagerInternal;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        PluginManagerInternal.addPlugin(
                Path.of("F:\\Minecraft Forge Projects\\Classloading\\ExamplePlugin\\build\\libs\\ExamplePlugin.jar"),
                "org.mangorage.plugin.ExamplePlugin"
        );

        PluginManagerInternal.load();

        new Thread(() -> {
            while (true) {
                PluginManagerInternal.post(new ExampleEvent());
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(5000);

                System.out.println("Reloading");
                PluginManagerInternal.unload();
                PluginManagerInternal.load();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
