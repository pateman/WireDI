package pl.pateman.wiredi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class WireComponentRegistry {
    private final Map<Class<?>, Object> singletons;
    private final Map<Class<?>, List<Object>> instances;

    public WireComponentRegistry() {
        singletons = new ConcurrentHashMap<>();
        instances = new HashMap<>();
    }

    private void putInstance(WireComponentInfo wireComponentInfo, Object instance) {
        List<Object> objects = instances.computeIfAbsent(wireComponentInfo.getClz(), (k) -> new ArrayList<>());
        objects.add(instance);
    }

    private void putSingleton(WireComponentInfo wireComponentInfo, Object instance) {
        singletons.putIfAbsent(wireComponentInfo.getClz(), instance);
    }

    public Optional<Object> getInstance(WireComponentInfo wireComponentInfo) {
        if (wireComponentInfo.isMultipleAllowed()) {
            return Optional.empty();
        }
        return Optional.ofNullable(singletons.get(wireComponentInfo.getClz()));
    }

    public void addInstance(WireComponentInfo wireComponentInfo, Object instance) {
        if (wireComponentInfo.isMultipleAllowed()) {
            putInstance(wireComponentInfo, instance);
            return;
        }
        putSingleton(wireComponentInfo, instance);
    }
}
