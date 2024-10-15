package org.mangorage.classloader.api;

import org.mangorage.classloader.internal.PluginMetadataImpl;

import java.net.URL;
import java.util.List;

public sealed interface IPluginMetadata permits PluginMetadataImpl {
    String mainClass();
    String pluginId();
    URL pluginURL();
    List<? extends IDependencyInfo> dependencies();
}
