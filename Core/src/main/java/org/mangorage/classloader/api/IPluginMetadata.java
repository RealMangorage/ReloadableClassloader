package org.mangorage.classloader.api;

import org.mangorage.classloader.internal.PluginMetadataImpl;

import java.net.URL;

public sealed interface IPluginMetadata permits PluginMetadataImpl {
    String mainClass();
    URL pluginURL();
}
