package pl.pateman.wiredi;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultWiringContext implements WiringContext {
    private final WireComponentInfoResolver componentInfoResolver;
    private final DefaultWireComponentFactory componentFactory;
    private final WireComponentRegistry componentRegistry;
    private final Map<String, Class<?>> wireComponents;
    private final Map<String, Set<String>> componentHierarchy;
    private final Set<Class<?>> classesInWiring;

    public DefaultWiringContext(WireComponentInfoResolver componentInfoResolver, DefaultWireComponentFactory componentFactory, WireComponentRegistry componentRegistry, List<Class<?>> scannedClasses) {
        this.componentInfoResolver = componentInfoResolver;
        this.componentFactory = componentFactory;
        this.componentFactory.assignWiringContext(this);
        this.componentRegistry = componentRegistry;
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
        componentHierarchy = WireComponentHierarchyDiscovery.findHierarchy(wireComponents);
        classesInWiring = ConcurrentHashMap.newKeySet();
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(Class<T> clz) {
        //  TODO Lifecycle methods - afterInit, preDestroy

        if (classesInWiring.contains(clz)) {
            throw new DIException("Circular dependency on '" + clz + "' detected");
        }

        WireComponentInfo componentInfo = componentInfoResolver.getComponentInfo(clz);
        Optional<Object> cachedObject = componentRegistry.getInstance(componentInfo);
        if (cachedObject.isPresent()) {
            return (T) cachedObject.get();
        }
        classesInWiring.add(componentInfo.getClz());
        T component = componentFactory.createWireComponent(componentInfo);
        componentRegistry.addInstance(componentInfo, component);
        classesInWiring.remove(componentInfo.getClz());
        return component;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(resolveClassForWireName(wireName));
    }
}
