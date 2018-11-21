package pl.pateman.wiredi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class WireConstructorInjectionInfo {
    private final Constructor<?> constructor;
    private final Map<Class<?>, String> wiringParams;

    public WireConstructorInjectionInfo(Constructor<?> constructor) {
        if (constructor == null) {
            throw new IllegalArgumentException("A valid constructor is required");
        }
        this.constructor = constructor;
        wiringParams = new LinkedHashMap<>();
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void addWiringParam(Class<?> clz, Annotation[] annotations) {
        String wireName = WireNameResolver.resolve(clz, annotations);
        wiringParams.put(clz, wireName);
    }

    public Collection<String> getWiringParams() {
        return Collections.unmodifiableCollection(wiringParams.values());
    }
}
