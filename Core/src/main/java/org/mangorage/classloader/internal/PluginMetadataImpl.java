package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPluginMetadata;

import java.net.URL;

public record PluginMetadataImpl(String mainClass, String pluginId, URL pluginURL) implements IPluginMetadata {}
