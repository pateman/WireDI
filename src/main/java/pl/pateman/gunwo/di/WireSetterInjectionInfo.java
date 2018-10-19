package pl.pateman.gunwo.di;

import java.lang.reflect.Method;

class WireSetterInjectionInfo {
    private final Method method;
    private final String wireName;

    WireSetterInjectionInfo(Method method, String wireName) {
        this.method = method;
        this.wireName = wireName;
    }

    Method getMethod() {
        return method;
    }

    String getWireName() {
        return wireName;
    }
}
