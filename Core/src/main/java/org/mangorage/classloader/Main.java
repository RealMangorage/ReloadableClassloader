package org.mangorage.classloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mangorage.classloader.event.ExampleEvent;
import org.mangorage.classloader.internal.PluginInfo;
import org.mangorage.classloader.internal.PluginManagerInternal;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

public class Main {
    private static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) {

        List<Path> manualPlugins = List.of(
                Path.of("F:\\Minecraft Forge Projects\\Classloading\\ExamplePlugin\\build\\libs\\ExamplePlugin.jar"),
                Path.of("F:\\Minecraft Forge Projects\\Classloading\\ExamplePlugin2\\build\\libs\\ExamplePlugin2.jar")
        );

        manualPlugins.forEach(plp -> {
            try (var pluginFile = new ZipFile(plp.toFile())) {
                var infoEntry = pluginFile.getEntry("plugin.json");
                if (infoEntry == null) return;
                var info = GSON.fromJson(
                        new InputStreamReader(pluginFile.getInputStream(infoEntry)),
                        PluginInfo.class
                );
                PluginManagerInternal.loadPlugin(plp, info);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        PluginManagerInternal.enableAll();

        new Thread(() -> {
            while (true) {
                PluginManagerInternal.post(new ExampleEvent());
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(5000);

                System.out.println("Reloading");
                PluginManagerInternal.disableAll();
                PluginManagerInternal.enableAll();
            } catch (InterruptedException e) {
                System.out.println("Fatal Error");
            }
        }
    }
}
