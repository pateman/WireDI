package pl.pateman.wiredi.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;
import java.util.*;

public final class HasAssignedFields<T> extends BaseMatcher<T> {

    private final Set<String> fieldNames;

    public static <T> HasAssignedFields<T> hasAssignedFields(String... fieldNames) {
        return new HasAssignedFields<>(fieldNames);
    }

    private HasAssignedFields(String... fields) {
        fieldNames = new HashSet<>(Arrays.asList(fields));
    }

    private List<Field> getFields(Class<?> clz) {
        List<Field> fields = new ArrayList<>();
        while (clz != Object.class) {
            fields.addAll(Arrays.asList(clz.getDeclaredFields()));
            clz = clz.getSuperclass();
        }
        return fields;
    }

    private boolean isFieldAssigned(Field field, Object item) {
        try {
            field.setAccessible(true);
            return field.get(item) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean matches(Object item) {
        List<Field> availableFields = getFields(item.getClass());

        return availableFields
                .stream()
                .filter(f -> fieldNames.contains(f.getName()))
                .filter(f -> isFieldAssigned(f, item))
                .count() == fieldNames.size();
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendValueList("the following fields to be assigned: ", ",", " but they weren't", fieldNames);
    }
}
