package pl.pateman.wiredi.dto;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class WireComponentInfo {
    private final Class<?> clz;
    private final Method factoryMethod;
    private final boolean multipleAllowed;
    private WireConstructorInjectionInfo constructorInjectionInfo;
    private final List<WireFieldInjectionInfo> fieldInjectionInfo;
    private final List<WireSetterInjectionInfo> setterInjectionInfo;
    private WireLifecycleMethodsInfo lifecycleMethodsInfo;
    private final List<String> factoryMethodParamsInfo;

    public WireComponentInfo(Class<?> clz, boolean multipleAllowed) {
        this(clz, multipleAllowed, null);
    }

    public WireComponentInfo(Class<?> clz, boolean multipleAllowed, Method factoryMethod) {
        this.clz = clz;
        this.multipleAllowed = multipleAllowed;
        this.factoryMethod = factoryMethod;
        fieldInjectionInfo = new ArrayList<>();
        setterInjectionInfo = new ArrayList<>();
        factoryMethodParamsInfo = new ArrayList<>();
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

    public void setConstructorInjectionInfo(WireConstructorInjectionInfo constructorInjectionInfo) {
        this.constructorInjectionInfo = constructorInjectionInfo;
    }

    public void addFieldInjectionInfo(Collection<WireFieldInjectionInfo> collection) {
        fieldInjectionInfo.addAll(collection);
    }

    public List<WireFieldInjectionInfo> getFieldInjectionInfo() {
        return Collections.unmodifiableList(fieldInjectionInfo);
    }

    public boolean hasFieldInjectionInfo() {
        return !fieldInjectionInfo.isEmpty();
    }

    public void addSetterInjectionInfo(Collection<WireSetterInjectionInfo> collection) {
        setterInjectionInfo.addAll(collection);
    }

    public List<WireSetterInjectionInfo> getSetterInjectionInfo() {
        return Collections.unmodifiableList(setterInjectionInfo);
    }

    public boolean hasSetterInjectionInfo() {
        return !setterInjectionInfo.isEmpty();
    }

    public boolean hasLifecycleMethods() {
        return lifecycleMethodsInfo != null && (lifecycleMethodsInfo.hasAfterInit() || lifecycleMethodsInfo.hasBeforeDestroy());
    }

    public WireLifecycleMethodsInfo getLifecycleMethodsInfo() {
        return lifecycleMethodsInfo;
    }

    public void setLifecycleMethodsInfo(WireLifecycleMethodsInfo lifecycleMethodsInfo) {
        this.lifecycleMethodsInfo = lifecycleMethodsInfo;
    }

    public boolean hasFactoryMethod() {
        return factoryMethod != null;
    }

    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public void addFactoryMethodParam(String wireName) {
        factoryMethodParamsInfo.add(wireName);
    }

    public List<String> getFactoryMethodParamsInfo() {
        return Collections.unmodifiableList(factoryMethodParamsInfo);
    }
}
