package pl.pateman.gunwo.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WireComponentInfoResolver {

    private final Map<Class<?>, WireComponentInfo> componentInfo;

    private void determineConstructorInjection(WireComponentInfo wireComponentInfo) {
        Class<?> clz = wireComponentInfo.getClz();
        Constructor<?>[] constructors = clz.getConstructors();

        if (constructors.length == 0) {
            return;
        }

        for (Constructor<?> constructor : constructors) {
            Wire constructorWire = constructor.getAnnotation(Wire.class);
            if (constructorWire == null) {
                continue;
            }

            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            WireConstructorInjectionInfo constructorInjectionInfo = new WireConstructorInjectionInfo(constructor);

            for (int paramIdx = 0; paramIdx < parameterTypes.length; paramIdx++) {
                constructorInjectionInfo.addWiringParam(parameterTypes[paramIdx], parameterAnnotations[paramIdx]);
            }

            wireComponentInfo.setConstructorInjectionInfo(constructorInjectionInfo);
            return;
        }
    }

    private List<Field> getFields(Class<?> clz) {
        List<Field> fields = new ArrayList<>();
        while (clz != Object.class) {
            fields.addAll(Arrays.asList(clz.getDeclaredFields()));
            clz = clz.getSuperclass();
        }
        return fields;
    }

    private void determineFieldInjection(WireComponentInfo wireComponentInfo) {
        Class<?> clz = wireComponentInfo.getClz();
        List<Field> fields = getFields(clz);

        if (fields.isEmpty()) {
            return;
        }

        List<WireFieldInjectionInfo> wireFieldInjectionInfoList = fields
                .stream()
                .filter(f -> f.isAnnotationPresent(Wire.class))
                .map(f -> new WireFieldInjectionInfo(f, WireNameResolver.resolve(f)))
                .collect(Collectors.toList());
        wireComponentInfo.addFieldInjectionInfo(wireFieldInjectionInfoList);
    }

    private void fillWireComponentInfo(WireComponentInfo wireComponentInfo) {
        determineConstructorInjection(wireComponentInfo);
        determineFieldInjection(wireComponentInfo);
    }

    private WireComponentInfo resolveWireComponentInfo(Class<?> componentClass) {
        WireComponent wireComponent = componentClass.getAnnotation(WireComponent.class);
        WireComponentInfo wireComponentInfo = new WireComponentInfo(componentClass, wireComponent.multiple());
        fillWireComponentInfo(wireComponentInfo);

        return wireComponentInfo;
    }

    WireComponentInfoResolver() {
        componentInfo = new ConcurrentHashMap<>();
    }

    WireComponentInfo getComponentInfo(Class<?> componentClass) {
        return componentInfo.computeIfAbsent(componentClass, this::resolveWireComponentInfo);
    }

}
