package pl.pateman.gunwo.di;

import java.util.*;

public final class WireComponentHierarchyDiscovery {
    private WireComponentHierarchyDiscovery() {

    }

    private static List<Class<?>> computeHierarchy(Class<?> clz) {
        List<Class<?>> container = new ArrayList<>();
        while (true) {
            clz = clz.getSuperclass();
            if (clz == Object.class) {
                break;
            }
            container.add(clz);
        }
        return container;
    }

    public static Map<Class<?>, Set<Class<?>>> findHierarchy(Map<String, Class<?>> components) {
        Collection<Class<?>> componentClasses = components.values();

        Map<Class<?>, Set<Class<?>>> result = new HashMap<>();
        for (Class<?> componentClass : componentClasses) {
            Class<?>[] interfaces = componentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                Set<Class<?>> classes = result.computeIfAbsent(anInterface, (k) -> new HashSet<>());
                classes.add(componentClass);
            }

            List<Class<?>> hierarchy = computeHierarchy(componentClass);
            for (Class<?> aClass : hierarchy) {
                Set<Class<?>> classes = result.computeIfAbsent(aClass, (k) -> new HashSet<>());
                classes.add(componentClass);
            }
        }
        return result;
    }
}
