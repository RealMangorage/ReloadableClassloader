package org.mangorage.classloader.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ReloadableValue<T> {

    public static <T> ReloadableValue<T> of(Supplier<T> supplier) {
        return of(supplier, t -> {});
    }

    public static <T> ReloadableValue<T> of(Supplier<T> supplier, Consumer<T> unloader) {
        return new ReloadableValue<>(supplier, unloader);
    }

    private final Supplier<T> valueSupplier;
    private final Consumer<T> unloader;
    private volatile T value;

    private ReloadableValue(Supplier<T> valueSupplier, Consumer<T> unloader) {
        this.valueSupplier = valueSupplier;
        this.unloader = unloader;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    }

    public T get() {
        return value;
    }

    public void loadValue() {
        this.value = valueSupplier.get();
    }

    public void unloadValue() {
        this.unloader.accept(this.value);
        this.value = null;
    }
}
