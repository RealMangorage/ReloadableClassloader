package org.mangorage.classloader.api;

import org.mangorage.classloader.internal.DependencyInfo;

public sealed interface IDependencyInfo permits DependencyInfo {
    String id();
    boolean required();
}
