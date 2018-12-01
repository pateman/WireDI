package pl.pateman.wiredi.dto;

import java.lang.reflect.Method;

public final class WireLifecycleMethodsInfo {
    private final Method afterInitMethod;
    private final Method beforeDestroyMethod;

    public WireLifecycleMethodsInfo(Method afterInitMethod, Method beforeDestroyMethod) {
        this.afterInitMethod = afterInitMethod;
        this.beforeDestroyMethod = beforeDestroyMethod;
    }

    public boolean hasAfterInit() {
        return afterInitMethod != null;
    }

    public boolean hasBeforeDestroy() {
        return beforeDestroyMethod != null;
    }

    public Method getAfterInitMethod() {
        return afterInitMethod;
    }

    public Method getBeforeDestroyMethod() {
        return beforeDestroyMethod;
    }
}
