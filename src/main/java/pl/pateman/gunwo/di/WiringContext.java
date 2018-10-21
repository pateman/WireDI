package pl.pateman.gunwo.di;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WiringContext {
    private final WireComponentInfoResolver componentInfoResolver;
    private final WireComponentFactory componentFactory;
    private final WireComponentRegistry componentRegistry;
    private final Map<String, Class<?>> wireComponents;
    private final Map<Class<?>, Set<Class<?>>> componentHierarchy;

    public WiringContext(WireComponentInfoResolver componentInfoResolver, WireComponentFactory componentFactory, WireComponentRegistry componentRegistry, List<Class<?>> scannedClasses) {
        this.componentInfoResolver = componentInfoResolver;
        this.componentFactory = componentFactory;
        this.componentFactory.setContext(this);
        this.componentRegistry = componentRegistry;
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
        componentHierarchy = WireComponentHierarchyDiscovery.findHierarchy(wireComponents);
    }

    @SuppressWarnings("unchecked")
    public <T> T getWireComponent(Class<T> clz) {
        //  TODO Inheritance and support for interfaces
        //  TODO Circular dependency detection
        //  TODO Lifecycle methods - afterInit, preDestroy
        WireComponentInfo componentInfo = componentInfoResolver.getComponentInfo(clz);
        Optional<Object> cachedObject = componentRegistry.getInstance(componentInfo);
        if (cachedObject.isPresent()) {
            return (T) cachedObject.get();
        }
        T component = componentFactory.createWireComponent(componentInfo);
        componentRegistry.addInstance(componentInfo, component);
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(wireComponents.get(wireName));
    }
}
