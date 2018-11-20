package pl.pateman.wiredi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class WireComponentFactory {
    private WiringContext context;

    private Object getWireComponent(String wireName) {
        if (PrimitiveDefaults.isPrimitiveOrJavaType(wireName)) {
            return PrimitiveDefaults.getDefault(wireName);
        }
        return context.getWireComponent(wireName);
    }

    private void injectField(Object instance, WireFieldInjectionInfo fieldInjectionInfo) throws IllegalAccessException {
        Object wireComponent = getWireComponent(fieldInjectionInfo.getWireName());
        Field field = fieldInjectionInfo.getField();
        field.setAccessible(true);
        field.set(instance, wireComponent);
    }

    private void injectSetter(Object instance, WireSetterInjectionInfo setterInjectionInfo) throws InvocationTargetException, IllegalAccessException {
        Object wireComponent = getWireComponent(setterInjectionInfo.getWireName());
        Method method = setterInjectionInfo.getMethod();
        method.setAccessible(true);
        method.invoke(instance, wireComponent);
    }

    private void injectFields(Object instance, WireComponentInfo wireComponentInfo) throws IllegalAccessException {
        List<WireFieldInjectionInfo> fieldInjectionInfo = wireComponentInfo.getFieldInjectionInfo();
        for (WireFieldInjectionInfo injectionInfo : fieldInjectionInfo) {
            injectField(instance, injectionInfo);
        }
    }

    private void injectSetters(Object instance, WireComponentInfo wireComponentInfo) throws InvocationTargetException, IllegalAccessException {
        List<WireSetterInjectionInfo> setterInjectionInfo = wireComponentInfo.getSetterInjectionInfo();
        for (WireSetterInjectionInfo injectionInfo : setterInjectionInfo) {
            injectSetter(instance, injectionInfo);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(WireComponentInfo wireComponentInfo)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (!wireComponentInfo.hasConstructorInjection()) {
            return (T) wireComponentInfo.getClz().newInstance();
        }

        WireConstructorInjectionInfo constructorInjectionInfo = wireComponentInfo.getConstructorInjectionInfo();
        Constructor<?> constructor = constructorInjectionInfo.getConstructor();
        Object[] resolvedParameters = constructorInjectionInfo.getWiringParams()
                .stream()
                .map(this::getWireComponent)
                .toArray();
        return (T) constructor.newInstance(resolvedParameters);
    }

    public <T> T createWireComponent(WireComponentInfo wireComponentInfo) {
        try {
            T instance = instantiate(wireComponentInfo);
            injectFields(instance, wireComponentInfo);
            injectSetters(instance, wireComponentInfo);
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new DIException(e);
        }
    }

    void setContext(WiringContext wiringContext) {
        this.context = wiringContext;
    }
}
