package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IPluginMetadata;

import java.net.URL;

public record PluginMetadataImpl(String main, URL pl) implements IPluginMetadata {
    @Override
    public String mainClass() {
        return main;
    }

    @Override
    public URL pluginURL() {
        return pl;
    }
}
