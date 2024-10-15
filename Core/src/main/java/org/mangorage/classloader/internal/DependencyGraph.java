package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IDependencyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyGraph {
    private Map<String, Set<IDependencyInfo>> dependencies = new HashMap<>();

    public void addPlugin(String pluginId, Set<IDependencyInfo> dependencies) {
        this.dependencies.put(pluginId, dependencies);
    }

    public List<String> getTopologicalSortWithDependencyChecking() {
        List<String> sortedPlugins = new ArrayList<>();
        Map<String, Integer> inDegrees = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        // Initialize in-degrees
        for (Map.Entry<String, Set<IDependencyInfo>> entry : dependencies.entrySet()) {
            for (IDependencyInfo dependency : entry.getValue()) {
                inDegrees.put(dependency.id(), inDegrees.getOrDefault(dependency.id(), 0) + 1);
            }
            inDegrees.putIfAbsent(entry.getKey(), 0);
        }

        // Find nodes with zero in-degree and add them to the queue
        for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            String currentPlugin = queue.poll();
            sortedPlugins.add(currentPlugin);

            for (IDependencyInfo dependency : dependencies.getOrDefault(currentPlugin, Collections.emptySet())) {
                inDegrees.put(dependency.id(), inDegrees.get(dependency.id()) - 1);
                if (inDegrees.get(dependency.id()) == 0) {
                    queue.add(dependency.id());
                }
            }
        }

        // Check for missing required dependencies and remove plugins if necessary
        List<String> pluginsToRemove = new ArrayList<>();
        for (String pluginId : sortedPlugins) {
            if (dependencies.get(pluginId) == null)continue;
            Set<String> requiredDependencies = dependencies.get(pluginId).stream()
                    .filter(dep -> dep.required())
                    .map(IDependencyInfo::id)
                    .collect(Collectors.toSet());
            if (!requiredDependencies.isEmpty() && !requiredDependencies.containsAll(sortedPlugins)) {
                pluginsToRemove.add(pluginId);
            }
        }

        // Remove plugins that have missing required dependencies
        sortedPlugins.removeAll(pluginsToRemove);

        return sortedPlugins;
    }
}
