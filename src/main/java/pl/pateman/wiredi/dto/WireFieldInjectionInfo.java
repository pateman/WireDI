package pl.pateman.wiredi.dto;

import java.lang.reflect.Field;

public final class WireFieldInjectionInfo {
    private final Field field;
    private final String wireName;

    public WireFieldInjectionInfo(Field field, String wireName) {
        if (field == null) {
            throw new IllegalArgumentException("A valid field is required");
        }
        this.field = field;
        this.wireName = wireName;
    }

    public Field getField() {
        return field;
    }

    public String getWireName() {
        return wireName;
    }
}
