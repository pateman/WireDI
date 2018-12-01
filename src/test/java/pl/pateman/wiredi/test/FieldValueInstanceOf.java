package pl.pateman.wiredi.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public final class FieldValueInstanceOf<T> extends BaseMatcher<T> {

    private final String fieldName;
    private final Class<?> fieldValueClass;

    public static <T> FieldValueInstanceOf<T> fieldValueInstanceOf(String fieldName, Class<?> fieldValueClass) {
        return new FieldValueInstanceOf<>(fieldName, fieldValueClass);
    }

    private FieldValueInstanceOf(String fieldName, Class<?> fieldValueClass) {
        this.fieldName = fieldName;
        this.fieldValueClass = fieldValueClass;
    }

    private Field getDesiredField(Class<?> clz) {
        Field field = null;
        while (clz != Object.class) {
            Optional<Field> foundField = Arrays
                    .stream(clz.getDeclaredFields())
                    .filter(f -> fieldName.equals(f.getName()))
                    .findFirst();
            if (foundField.isPresent()) {
                field = foundField.get();
                break;
            }
            clz = clz.getSuperclass();
        }
        return field;
    }

    private Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public boolean matches(Object item) {
        Field field = getDesiredField(item.getClass());
        if (field == null) {
            return false;
        }
        field.setAccessible(true);
        Object fieldValue = getFieldValue(field, item);
        if (fieldValue == null) {
            return false;
        }
        return fieldValueClass.isInstance(fieldValue);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the field '" + fieldName + "' value to be an instance of '" + fieldValueClass + "'");
    }
}
