package pl.pateman.wiredi.core;

import pl.pateman.wiredi.dto.WireComponentInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WireComponentRegistry {
    private final Map<Class<?>, Object> singletons;
    private final Map<Class<?>, List<Object>> instances;

    public WireComponentRegistry() {
        singletons = new ConcurrentHashMap<>();
        instances = new ConcurrentHashMap<>();
    }

    private void putInstance(WireComponentInfo wireComponentInfo, Object instance) {
        List<Object> objects = instances.computeIfAbsent(wireComponentInfo.getClz(), (k) -> Collections.synchronizedList(new ArrayList<>()));
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

    public List<Object> getComponentsByClass(Class<?> clz) {
        if (singletons.containsKey(clz)) {
            return Collections.singletonList(singletons.get(clz));
        }
        if (instances.containsKey(clz)) {
            return Collections.unmodifiableList(instances.get(clz));
        }
        return Collections.emptyList();
    }

    public Set<Class<?>> getComponentClasses() {
        return Stream
                .concat(singletons.keySet().stream(), instances.keySet().stream())
                .collect(Collectors.toSet());
    }
}
