package pl.pateman.wiredi.core;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class WireNameResolver {
    private WireNameResolver() {

    }

    public static String resolve(Field field) {
        Wire wire = field.getAnnotation(Wire.class);
        if (wire != null && !wire.name().isEmpty()) {
            return wire.name();
        }
        return field.getType().getCanonicalName();
    }

    public static String resolve(Class<?> clz, Annotation[] annotations) {
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

    public static String resolve(Method setter) {
        Wire setterAnnotation = setter.getAnnotation(Wire.class);
        if (setterAnnotation != null && !setterAnnotation.name().isEmpty()) {
            return setterAnnotation.name();
        }

        Parameter parameter = setter.getParameters()[0];
        WireName annotation = parameter.getAnnotation(WireName.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }

        return parameter.getType().getCanonicalName();
    }
}
