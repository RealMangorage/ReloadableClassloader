package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IDependencyInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DependencyGraph {

    public static void main(String[] args) {
        var dependencyInfosPluginB = List.of(
                new DependencyInfo("PluginA", true)
        );
        var dependencyInfosPluginC = List.of(
                new DependencyInfo("PluginB", true)
        );

        DependencyGraph graph = new DependencyGraph();
        graph.addPlugin("PluginA", List.of());
        graph.addPlugin("PluginB", dependencyInfosPluginB);
        graph.addPlugin("PluginC", dependencyInfosPluginC);

        var result = graph.compute();
        result.forEach(System.out::println);
    }

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
        return list = compute()
                .stream()
                .map(containerMap::get)
                .toList();
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

        boolean allRequiredLoaded = true;
        for (IDependencyInfo dep : dependencies.get(plugin)) {
            if (!isPluginLoaded(dep.id(), loadedPlugins)) {
                loadPlugin(dep.id(), loadedPlugins, result);
                if (dep.required() && !isPluginLoaded(dep.id(), loadedPlugins)) {
                    allRequiredLoaded = false;
                    break;
                }
            }
        }

        if (allRequiredLoaded) {
            loadedPlugins.add(plugin);
            result.add(plugin);
        }
    }
}

