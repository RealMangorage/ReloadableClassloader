package org.mangorage.classloader.internal;

import org.mangorage.classloader.api.IDependencyInfo;

public record DependencyInfo(String id, boolean required) implements IDependencyInfo {}
