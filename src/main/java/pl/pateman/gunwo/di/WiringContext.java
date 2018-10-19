package pl.pateman.gunwo.di;

import java.util.List;
import java.util.Map;

public class WiringContext {
    private final WireComponentInfoResolver componentInfoResolver;
    private final WireComponentFactory componentFactory;
    private final Map<String, Class<?>> wireComponents;

    public WiringContext(WireComponentInfoResolver componentInfoResolver, WireComponentFactory componentFactory, List<Class<?>> scannedClasses) {
        this.componentInfoResolver = componentInfoResolver;
        this.componentFactory = componentFactory;
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
    }

    public <T> T getWireComponent(Class<T> clz) {
        //  TODO Component caching
        //  TODO Inheritance and support for interfaces
        //  TODO Circular dependency detection
        //  TODO Lifecycle methods - afterInit, preDestroy
        WireComponentInfo componentInfo = componentInfoResolver.getComponentInfo(clz);
        return componentFactory.createWireComponent(componentInfo);
    }

    @SuppressWarnings("unchecked")
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(wireComponents.get(wireName));
    }
}
