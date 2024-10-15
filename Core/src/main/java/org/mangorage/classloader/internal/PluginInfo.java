package org.mangorage.classloader.internal;

import java.util.List;

public record PluginInfo(
        String pluginId,
        String mainClass,
        List<DependencyInfo> depends
) {}
