package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IDependencyInfo;
import org.mangorage.classloader.api.IPluginMetadata;

import java.net.URL;
import java.util.List;

public record PluginMetadataImpl(String mainClass, String pluginId, URL pluginURL, List<? extends IDependencyInfo> dependencies) implements IPluginMetadata { }
