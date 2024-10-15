package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IDependencyInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DependencyGraph {

    private final Map<String, List<? extends IDependencyInfo>> dependencies = new ConcurrentHashMap<>();
    private List<PluginContainerImpl> list = null;

    public void addPlugin(String pluginId, List<? extends IDependencyInfo> dependencyInfos) {
        dependencies.put(pluginId, dependencyInfos == null ? List.of() : dependencyInfos);
        list = null;
    }

    public void removePlugin(String pluginId) {
        dependencies.remove(pluginId);
        list = null;
    }

    public List<PluginContainerImpl> computeContainerList(Map<String, PluginContainerImpl> containerMap) {
        if (this.list != null)
            return this.list;
        var list = compute()
                .stream()
                .map(containerMap::get)
                .toList();
        return list;
    }

    public List<String> compute() {
        Set<String> loadedPlugins = new HashSet<>();
        List<String> result = new ArrayList<>();

        for (String plugin : dependencies.keySet()) {
            if (!isPluginLoaded(plugin, loadedPlugins)) {
                loadPlugin(plugin, loadedPlugins, result);
            }
        }

        return result;
    }

    private boolean isPluginLoaded(String plugin, Set<String> loadedPlugins) {
        return loadedPlugins.contains(plugin);
    }

    private void loadPlugin(String plugin, Set<String> loadedPlugins, List<String> result) {
        if (!dependencies.containsKey(plugin)) return;

        for (IDependencyInfo dep : dependencies.get(plugin)) {
            if (!isPluginLoaded(dep.id(), loadedPlugins)) {
                loadPlugin(dep.id(), loadedPlugins, result);
            }
        }

        // Ensure all required dependencies are loaded
        boolean allRequiredDependenciesLoaded = dependencies.get(plugin).stream()
                .filter(IDependencyInfo::required)
                .allMatch(dep -> isPluginLoaded(dep.id(), loadedPlugins));

        if (allRequiredDependenciesLoaded) {
            loadedPlugins.add(plugin);
            result.add(plugin);

            // Ensure optional dependencies are loaded after the plugin
            dependencies.get(plugin).stream()
                    .filter(dep -> !dep.required())
                    .forEach(dep -> {
                        if (!isPluginLoaded(dep.id(), loadedPlugins)) {
                            loadPlugin(dep.id(), loadedPlugins, result);
                        }
                    });
        }
    }
}

