package pl.pateman.wiredi.core;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireAfterInit;
import pl.pateman.wiredi.annotation.WireBeforeDestroy;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.dto.*;
import pl.pateman.wiredi.exception.DIException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class WireComponentInfoResolver {

    private final Map<Class<?>, WireComponentInfo> componentInfo;

    private void determineConstructorInjection(WireComponentInfo wireComponentInfo) {
        Class<?> clz = wireComponentInfo.getClz();
        Constructor<?>[] constructors = clz.getDeclaredConstructors();

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

    private List<Method> getDeclaredMethods(Class<?> clz) {
        List<Method> methods = new ArrayList<>();
        while (clz != Object.class) {
            methods.addAll(Arrays.asList(clz.getDeclaredMethods()));
            clz = clz.getSuperclass();
        }
        return methods;
    }

    private List<Method> getSetters(Class<?> clz) {
        return getDeclaredMethods(clz)
                .stream()
                .filter(mtd -> mtd.getName().startsWith("set"))
                .filter(mtd -> mtd.getParameterCount() == 1)
                .collect(Collectors.toList());
    }

    private boolean isLifecycleMethod(Method method) {
        return (method.isAnnotationPresent(WireAfterInit.class) || method.isAnnotationPresent(WireBeforeDestroy.class)) && method.getParameterCount() == 0;
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

    private void determineSetterInjection(WireComponentInfo wireComponentInfo) {
        Class<?> clz = wireComponentInfo.getClz();
        List<Method> setters = getSetters(clz);

        if (setters.isEmpty()) {
            return;
        }

        List<WireSetterInjectionInfo> setterInjectionInfoList = setters
                .stream()
                .filter(m -> m.isAnnotationPresent(Wire.class))
                .map(m -> new WireSetterInjectionInfo(m, WireNameResolver.resolve(m)))
                .collect(Collectors.toList());
        wireComponentInfo.addSetterInjectionInfo(setterInjectionInfoList);
    }

    private void determineLifecycleMethods(WireComponentInfo wireComponentInfo) {
        List<Method> methods = getDeclaredMethods(wireComponentInfo.getClz());

        if (methods.isEmpty()) {
            return;
        }

        List<Method> lifecycleMethods = methods
                .stream()
                .filter(this::isLifecycleMethod)
                .collect(Collectors.toList());
        if (lifecycleMethods.isEmpty()) {
            return;
        }

        Optional<Method> afterInit = lifecycleMethods.stream().filter(m -> m.isAnnotationPresent(WireAfterInit.class)).findFirst();
        Optional<Method> beforeDestroy = lifecycleMethods.stream().filter(m -> m.isAnnotationPresent(WireBeforeDestroy.class)).findFirst();

        WireLifecycleMethodsInfo wireLifecycleMethodsInfo = new WireLifecycleMethodsInfo(afterInit.orElse(null), beforeDestroy.orElse(null));
        wireComponentInfo.setLifecycleMethodsInfo(wireLifecycleMethodsInfo);
    }

    private void fillWireComponentInfo(WireComponentInfo wireComponentInfo) {
        determineConstructorInjection(wireComponentInfo);
        determineFieldInjection(wireComponentInfo);
        determineSetterInjection(wireComponentInfo);
        determineLifecycleMethods(wireComponentInfo);
    }

    private WireComponentInfo resolveWireComponentInfo(Class<?> componentClass) {
        WireComponent wireComponent = componentClass.getAnnotation(WireComponent.class);
        if (wireComponent == null) {
            throw new DIException(componentClass + " is not annotated with WireComponent");
        }
        WireComponentInfo wireComponentInfo = new WireComponentInfo(componentClass, wireComponent.multiple());
        fillWireComponentInfo(wireComponentInfo);

        return wireComponentInfo;
    }

    public WireComponentInfoResolver() {
        componentInfo = new ConcurrentHashMap<>();
    }

    public WireComponentInfo getComponentInfo(Class<?> componentClass) {
        return componentInfo.computeIfAbsent(componentClass, this::resolveWireComponentInfo);
    }

}
