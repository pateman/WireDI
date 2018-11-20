package pl.pateman.wiredi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class WireComponentInfo {
    private final Class<?> clz;
    private final boolean multipleAllowed;
    private WireConstructorInjectionInfo constructorInjectionInfo;
    private final List<WireFieldInjectionInfo> fieldInjectionInfo;
    private final List<WireSetterInjectionInfo> setterInjectionInfo;

    WireComponentInfo(Class<?> clz, boolean multipleAllowed) {
        this.clz = clz;
        this.multipleAllowed = multipleAllowed;
        fieldInjectionInfo = new ArrayList<>();
        setterInjectionInfo = new ArrayList<>();
    }

    Class<?> getClz() {
        return clz;
    }

    boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    boolean hasConstructorInjection() {
        return constructorInjectionInfo != null;
    }

    WireConstructorInjectionInfo getConstructorInjectionInfo() {
        return constructorInjectionInfo;
    }

    void setConstructorInjectionInfo(WireConstructorInjectionInfo constructorInjectionInfo) {
        this.constructorInjectionInfo = constructorInjectionInfo;
    }

    void addFieldInjectionInfo(Collection<WireFieldInjectionInfo> collection) {
        fieldInjectionInfo.addAll(collection);
    }

    List<WireFieldInjectionInfo> getFieldInjectionInfo() {
        return fieldInjectionInfo;
    }

    void addSetterInjectionInfo(Collection<WireSetterInjectionInfo> collection) {
        setterInjectionInfo.addAll(collection);
    }

    List<WireSetterInjectionInfo> getSetterInjectionInfo() {
        return setterInjectionInfo;
    }
}