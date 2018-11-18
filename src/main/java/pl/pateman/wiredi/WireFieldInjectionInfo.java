package pl.pateman.wiredi;

import java.lang.reflect.Field;

class WireFieldInjectionInfo {
    private final Field field;
    private final String wireName;

    WireFieldInjectionInfo(Field field, String wireName) {
        this.field = field;
        this.wireName = wireName;
    }

    Field getField() {
        return field;
    }

    String getWireName() {
        return wireName;
    }
}
