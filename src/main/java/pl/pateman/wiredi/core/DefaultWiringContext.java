package pl.pateman.wiredi.core;

import pl.pateman.wiredi.ComponentInfoRegistry;
import pl.pateman.wiredi.WireComponentFactory;
import pl.pateman.wiredi.WiringContext;
import pl.pateman.wiredi.dto.WireComponentInfo;
import pl.pateman.wiredi.dto.WireFieldInjectionInfo;
import pl.pateman.wiredi.exception.DIException;
import pl.pateman.wiredi.exception.WireNameClassResolveException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.*;

public class DefaultWiringContext implements WiringContext {
    private final ComponentInfoRegistry componentInfoRegistry;
    private final WireComponentFactory componentFactory;
    private final WireComponentRegistry componentRegistry;
    private final Map<String, Method> componentsInWires;
    private final Map<Class<?>, String> componentInWiresMapping;
    private final Map<String, Class<?>> wireComponents;
    private final Map<String, Set<String>> componentHierarchy;
    private final Set<Class<?>> classesInWiring;
    private final Map<Class<?>, Object> locks;

    public DefaultWiringContext(ComponentInfoRegistry componentInfoRegistry, WireComponentFactory componentFactory, WireComponentRegistry componentRegistry, List<Class<?>> scannedClasses) {
        this.componentInfoRegistry = componentInfoRegistry;
        this.componentFactory = componentFactory;
        this.componentFactory.assignWiringContext(this);
        this.componentRegistry = componentRegistry;

        componentsInWires = WireComponentDiscovery.findWireComponentsInWires(scannedClasses);
        componentInWiresMapping = prepareComponentsInWiresMapping();
        wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
        mergeComponents();

        componentHierarchy = WireComponentHierarchyDiscovery.findHierarchy(wireComponents);
        classesInWiring = ConcurrentHashMap.newKeySet();
        locks = new ConcurrentHashMap<>();
    }

    private void mergeComponents() {
        componentsInWires.forEach((k, v) -> wireComponents.put(k, v.getReturnType()));
    }

    private Map<Class<?>, String> prepareComponentsInWiresMapping() {
        return componentsInWires.entrySet().stream()
                .map(e -> new SimpleEntry<Class<?>, String>(e.getValue().getReturnType(), e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Class<?> resolveClassForWireName(String wireName) {
        Method factoryMethod = componentsInWires.get(wireName);
        if (factoryMethod != null) {
            return factoryMethod.getReturnType();
        }
        Class<?> clz = wireComponents.get(wireName);
        if (clz != null) {
            return clz;
        }
        Set<String> hierarchy = componentHierarchy.get(wireName);
        if (hierarchy == null) {
            throw new WireNameClassResolveException("Unknown wire name '" + wireName + "'");
        }

        if (hierarchy.size() > 1) {
            throw new WireNameClassResolveException("Ambiguous wire name '" + wireName + "' - matches components: " + hierarchy);
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
        componentFactory.invokeAfterInit(component, componentInfo);
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

    private void invokeBeforeDestroy(Object instance, WireComponentInfo wireComponentInfo) {
        Method beforeDestroyMethod = wireComponentInfo.getLifecycleMethodsInfo().getBeforeDestroyMethod();
        beforeDestroyMethod.setAccessible(true);
        try {
            beforeDestroyMethod.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DIException("Unable to invoke @WireBeforeDestroy on a component of class '" + wireComponentInfo.getClz() + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(Class<T> clz) {
        if (classesInWiring.contains(clz)) {
            throw new DIException("Circular dependency on '" + clz + "' detected");
        }

        WireComponentInfo componentInfo;
        String wiresMapping = componentInWiresMapping.get(clz);
        if (wiresMapping != null) {
            Method factoryMethod = componentsInWires.get(wiresMapping);
            componentInfo = componentInfoRegistry.getComponentInfo(factoryMethod);
        } else {
            componentInfo = componentInfoRegistry.getComponentInfo(clz);
        }
        return wire(componentInfo);
    }

    private <T> T wire(WireComponentInfo componentInfo) {
        if (!componentInfo.isMultipleAllowed()) {
            return wireSingletonComponent(componentInfo);
        }

        return createComponent(componentInfo);
    }

    @Override
    public void destroy() {
        Set<Class<?>> wiredClasses = componentRegistry.getComponentClasses();
        Set<WireComponentInfo> componentsWithBeforeDestroy = wiredClasses
                .stream()
                .map(componentInfoRegistry::getComponentInfo)
                .filter(WireComponentInfo::hasLifecycleMethods)
                .filter(wireComponentInfo -> wireComponentInfo.getLifecycleMethodsInfo().hasBeforeDestroy())
                .collect(toSet());

        for (WireComponentInfo componentInfo : componentsWithBeforeDestroy) {
            List<Object> componentsByClass = componentRegistry.getComponentsByClass(componentInfo.getClz());
            for (Object component : componentsByClass) {
                invokeBeforeDestroy(component, componentInfo);
            }
        }
    }

    @Override
    public <T> Collection<T> getWireComponentsOfType(Class<T> clz) {
        Set<String> wireNames = componentHierarchy.get(clz.getCanonicalName());
        if (wireNames == null || wireNames.isEmpty()) {
            return Collections.emptyList();
        }

        return wireNames
                .stream()
                .map(this::getWireComponent)
                .map(clz::cast)
                .collect(toList());
    }

    @Override
    public void addDynamicWire(String wire, Object instance) {
        Map<String, Class<?>> map = Collections.singletonMap(wire, instance.getClass());

        wireComponents.putAll(map);
        Map<String, Set<String>> hierarchy = WireComponentHierarchyDiscovery.findHierarchy(map);
        componentHierarchy.putAll(hierarchy);

        try {
            for (String clzName : hierarchy.keySet()) {
                Class<?> aClass = Class.forName(clzName);
                WireComponentInfo componentInfo = componentInfoRegistry.addDynamicWireComponentInfo(aClass);
                componentRegistry.addInstance(componentInfo, instance);
            }
        } catch (ClassNotFoundException e) {
            throw new DIException("Unable to add dynamic wire", e);
        }

        wireDynamicFields(wire, instance);
    }

    private void wireDynamicFields(String wire, Object instance) {
        Map<Class<?>, List<WireFieldInjectionInfo>> classesWithDynamicFields = componentInfoRegistry.getComponentsInfo(
                wci -> wci.getFieldInjectionInfo().stream().anyMatch(WireFieldInjectionInfo::isDynamic)).stream()
                .collect(toMap(WireComponentInfo::getClz, WireComponentInfo::getFieldInjectionInfo));

        classesWithDynamicFields.forEach((clz, fieldInfo) -> {
            List<Field> fieldsToSet = fieldInfo.stream()
                    .filter(info -> wire.equals(info.getWireName()))
                    .map(WireFieldInjectionInfo::getField)
                    .collect(toList());
            if (fieldsToSet.isEmpty()) {
                return;
            }

            for (Object component : componentRegistry.getComponentsByClass(clz)) {
                fieldsToSet.forEach(f -> wireField(component, f, instance));
            }
        });
    }

    private void wireField(Object component, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(component, value);
        } catch (IllegalAccessException e) {
            throw new DIException("Unable to wire dynamic field", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getWireComponent(String wireName) {
        return (T) getWireComponent(resolveClassForWireName(wireName));
    }
}
