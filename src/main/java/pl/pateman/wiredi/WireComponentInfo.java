package pl.pateman.wiredi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class WireComponentInfo {
    private final Class<?> clz;
    private final boolean multipleAllowed;
    private WireConstructorInjectionInfo constructorInjectionInfo;
    private final List<WireFieldInjectionInfo> fieldInjectionInfo;
    private final List<WireSetterInjectionInfo> setterInjectionInfo;

    public WireComponentInfo(Class<?> clz, boolean multipleAllowed) {
        this.clz = clz;
        this.multipleAllowed = multipleAllowed;
        fieldInjectionInfo = new ArrayList<>();
        setterInjectionInfo = new ArrayList<>();
    }

    public Class<?> getClz() {
        return clz;
    }

    public boolean isMultipleAllowed() {
        return multipleAllowed;
    }

    public boolean hasConstructorInjection() {
        return constructorInjectionInfo != null;
    }

    public WireConstructorInjectionInfo getConstructorInjectionInfo() {
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

    boolean hasFieldInjectionInfo() {
        return !fieldInjectionInfo.isEmpty();
    }

    void addSetterInjectionInfo(Collection<WireSetterInjectionInfo> collection) {
        setterInjectionInfo.addAll(collection);
    }

    List<WireSetterInjectionInfo> getSetterInjectionInfo() {
        return setterInjectionInfo;
    }

    boolean hasSetterInjectionInfo() {
        return !setterInjectionInfo.isEmpty();
    }
}
