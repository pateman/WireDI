package pl.pateman.gunwo.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class WiringContext {
    private final WireComponentInfoResolver componentInfoResolver;
    private final Map<String, Class<?>> wireComponents;

    public WiringContext(WireComponentInfoResolver componentInfoResolver, List<Class<?>> scannedClasses) {
        this.componentInfoResolver = componentInfoResolver;
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(WireComponentInfo wireComponentInfo)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (!wireComponentInfo.hasConstructorInjection()) {
            return (T) wireComponentInfo.getClz().newInstance();
        }

        WireConstructorInjectionInfo constructorInjectionInfo = wireComponentInfo.getConstructorInjectionInfo();
        Constructor<?> constructor = constructorInjectionInfo.getConstructor();
        Object[] resolvedParameters = constructorInjectionInfo.getWiringParams()
                .stream()
                .map(this::getWireComponent)
                .toArray();
        return (T) constructor.newInstance(resolvedParameters);
    }

    public <T> T getWireComponent(Class<T> clz) {
        //  TODO Field injection and setter injection
        //  TODO Circular dependency detection
        //  TODO Component caching
        //  TODO Lifecycle methods - afterInit, preDestroy
        WireComponentInfo componentInfo = componentInfoResolver.getComponentInfo(clz);
        try {
            return instantiate(componentInfo);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new DIException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(wireComponents.get(wireName));
    }
}
