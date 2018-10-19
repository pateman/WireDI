package pl.pateman.gunwo.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class WireNameResolver {
    private WireNameResolver() {

    }

    static String resolve(Field field) {
        Wire wire = field.getAnnotation(Wire.class);
        if (wire != null && !wire.name().isEmpty()) {
            return wire.name();
        }
        return field.getType().getCanonicalName();
    }

    static String resolve(Class<?> clz, Annotation[] annotations) {
        if (annotations == null) {
            return clz.getCanonicalName();
        }

        for (int i = 0; i < annotations.length; i++) {
            if (!(annotations[i] instanceof WireName)) {
                continue;
            }
            WireName wireName = (WireName) annotations[i];
            if (wireName != null && !wireName.value().isEmpty()) {
                return wireName.value();
            }
        }

        return clz.getCanonicalName();
    }

    static String resolve(Method setter) {
        Wire annotation = setter.getAnnotation(Wire.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }

        return setter.getParameters()[0].getType().getCanonicalName();
    }
}
