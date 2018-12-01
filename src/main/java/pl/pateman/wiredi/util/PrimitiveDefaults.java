package pl.pateman.wiredi.util;

import pl.pateman.wiredi.exception.DIException;

import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public final class PrimitiveDefaults {
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAMES = Stream
            .of(new SimpleEntry<>("boolean", boolean.class),
                    new SimpleEntry<>("byte", byte.class),
                    new SimpleEntry<>("char", char.class),
                    new SimpleEntry<>("double", double.class),
                    new SimpleEntry<>("float", float.class),
                    new SimpleEntry<>("int", int.class),
                    new SimpleEntry<>("long", long.class),
                    new SimpleEntry<>("short", short.class))
            .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    private static final Map<Class<?>, Object> DEFAULT_VALUES = PRIMITIVE_TYPE_NAMES.values()
            .stream()
            .collect(toMap(clazz -> (Class<?>) clazz, clazz -> Array.get(Array.newInstance(clazz, 1), 0)));

    private PrimitiveDefaults() {

    }

    public static boolean isPrimitive(String wireName) {
        return PRIMITIVE_TYPE_NAMES.containsKey(wireName);
    }

    public static boolean isJavaType(String wireName) {
        return wireName.startsWith("java.lang");
    }

    public static boolean isPrimitiveOrJavaType(String wireName) {
        return isPrimitive(wireName) || isJavaType(wireName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDefault(String wireName) {
        try {
            if (isPrimitive(wireName)) {
                return (T) DEFAULT_VALUES.get(PRIMITIVE_TYPE_NAMES.get(wireName));
            } else if (isJavaType(wireName)) {
                return (T) DEFAULT_VALUES.get(Class.forName(wireName));
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new DIException("Unable to get the default value for wire '" + wireName + "'", e);
        }
    }
}