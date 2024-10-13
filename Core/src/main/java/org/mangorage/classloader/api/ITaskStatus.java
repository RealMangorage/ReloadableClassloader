package org.mangorage.classloader.api;

import java.util.UUID;

public interface ITaskStatus {
    boolean isRunning();
    UUID getId();
}
