package pl.pateman.wiredi.dto;

import java.lang.reflect.Field;

public final class WireFieldInjectionInfo {
    private final Field field;
    private final String wireName;
    private final boolean dynamic;

    public WireFieldInjectionInfo(Field field, String wireName, boolean dynamic) {
        if (field == null) {
            throw new IllegalArgumentException("A valid field is required");
        }
        this.field = field;
        this.wireName = wireName;
        this.dynamic = dynamic;
    }

    public Field getField() {
        return field;
    }

    public String getWireName() {
        return wireName;
    }

    public boolean isDynamic() {
        return dynamic;
    }
}
