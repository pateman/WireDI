package pl.pateman.wiredi.core;

import pl.pateman.wiredi.WireComponentFactory;
import pl.pateman.wiredi.WiringContext;
import pl.pateman.wiredi.dto.WireComponentInfo;
import pl.pateman.wiredi.exception.DIException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultWiringContext implements WiringContext {
    private final WireComponentInfoResolver componentInfoResolver;
    private final WireComponentFactory componentFactory;
    private final WireComponentRegistry componentRegistry;
    private final Map<String, Class<?>> wireComponents;
    private final Map<String, Set<String>> componentHierarchy;
    private final Set<Class<?>> classesInWiring;
    private final Map<Class<?>, Object> locks;

    public DefaultWiringContext(WireComponentInfoResolver componentInfoResolver, WireComponentFactory componentFactory, WireComponentRegistry componentRegistry, List<Class<?>> scannedClasses) {
        this.componentInfoResolver = componentInfoResolver;
        this.componentFactory = componentFactory;
        this.componentFactory.assignWiringContext(this);
        this.componentRegistry = componentRegistry;
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
        componentHierarchy = WireComponentHierarchyDiscovery.findHierarchy(wireComponents);
        classesInWiring = ConcurrentHashMap.newKeySet();
        locks = new ConcurrentHashMap<>();
    }

    private Class<?> resolveClassForWireName(String wireName) {
        Class<?> clz = wireComponents.get(wireName);
        if (clz != null) {
            return clz;
        }
        Set<String> hierarchy = componentHierarchy.get(wireName);
        if (hierarchy == null) {
            throw new DIException("Unknown wire name '" + wireName + "'");
        }

        if (hierarchy.size() > 1) {
            throw new DIException("Ambiguous wire name '" + wireName + "' - matches components: " + hierarchy);
        }

        return wireComponents.get(hierarchy.iterator().next());
    }

    private Object getLock(Class<?> clz) {
        return locks.computeIfAbsent(clz, (k) -> new Object());
    }

    private <T> T createComponent(WireComponentInfo componentInfo) {
        classesInWiring.add(componentInfo.getClz());
        T component = componentFactory.createWireComponent(componentInfo);
        componentRegistry.addInstance(componentInfo, component);
        classesInWiring.remove(componentInfo.getClz());
        return component;
    }

    @SuppressWarnings("unchecked")
    private <T> T getComponentFromRegistry(WireComponentInfo componentInfo) {
        Optional<Object> cachedObject = componentRegistry.getInstance(componentInfo);
        return (T) cachedObject.orElse(null);
    }

    private <T> T wireSingletonComponent(WireComponentInfo componentInfo) {
        T cachedObject = getComponentFromRegistry(componentInfo);
        if (cachedObject != null) {
            return cachedObject;
        }

        synchronized (getLock(componentInfo.getClz())) {
            createComponent(componentInfo);
        }

        return getComponentFromRegistry(componentInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(Class<T> clz) {
        //  TODO Lifecycle methods - afterInit, preDestroy

        if (classesInWiring.contains(clz)) {
            throw new DIException("Circular dependency on '" + clz + "' detected");
        }

        WireComponentInfo componentInfo = componentInfoResolver.getComponentInfo(clz);
        if (!componentInfo.isMultipleAllowed()) {
            return wireSingletonComponent(componentInfo);
        }

        return createComponent(componentInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(resolveClassForWireName(wireName));
    }
}