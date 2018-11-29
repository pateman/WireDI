package pl.pateman.wiredi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

public final class WireConstructorInjectionInfo {
    private final Constructor<?> constructor;
    private final List<String> wiringParams;

    public WireConstructorInjectionInfo(Constructor<?> constructor) {
        if (constructor == null) {
            throw new IllegalArgumentException("A valid constructor is required");
        }
        this.constructor = constructor;
        wiringParams = new ArrayList<>();
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void addWiringParam(Class<?> clz, Annotation[] annotations) {
        String wireName = WireNameResolver.resolve(clz, annotations);
        wiringParams.add(wireName);
    }

    public Collection<String> getWiringParams() {
        return Collections.unmodifiableCollection(wiringParams);
    }
}
