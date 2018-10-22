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

    public static Map<String, Set<String>> findHierarchy(Map<String, Class<?>> components) {
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Class<?>> componentEntry : components.entrySet()) {
            Class<?> componentClass = componentEntry.getValue();
            String componentName = componentEntry.getKey();

            Class<?>[] interfaces = componentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                Set<String> classes = result.computeIfAbsent(anInterface.getCanonicalName(), (k) -> new HashSet<>());
                classes.add(componentName);
            }

            List<Class<?>> hierarchy = computeHierarchy(componentClass);
            for (Class<?> aClass : hierarchy) {
                Set<String> classes = result.computeIfAbsent(aClass.getCanonicalName(), (k) -> new HashSet<>());
                classes.add(componentName);
            }
        }
        return result;
    }
}
