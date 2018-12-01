package pl.pateman.wiredi.dto;

import java.lang.reflect.Method;

public final class WireSetterInjectionInfo {
    private final Method method;
    private final String wireName;

    public WireSetterInjectionInfo(Method method, String wireName) {
        if (method == null) {
            throw new IllegalArgumentException("A valid method is required");
        }
        this.method = method;
        this.wireName = wireName;
    }

    public Method getMethod() {
        return method;
    }

    public String getWireName() {
        return wireName;
    }
}
