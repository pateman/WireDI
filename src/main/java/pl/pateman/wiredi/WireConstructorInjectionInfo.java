package pl.pateman.wiredi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class WireConstructorInjectionInfo {
    private final Constructor<?> constructor;
    private final Map<Class<?>, String> wiringParams;

    WireConstructorInjectionInfo(Constructor<?> constructor) {
        this.constructor = constructor;
        wiringParams = new LinkedHashMap<>();
    }

    Constructor<?> getConstructor() {
        return constructor;
    }

    void addWiringParam(Class<?> clz, Annotation[] annotations) {
        String wireName = WireNameResolver.resolve(clz, annotations);
        wiringParams.put(clz, wireName);
    }

    Collection<String> getWiringParams() {
        return wiringParams.values();
    }
}
