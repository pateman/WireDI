package pl.pateman.wiredi;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class DefaultWireComponentFactory implements WireComponentFactory {
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

    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        singleoneInstanceField.setAccessible(true);
        return (Unsafe) singleoneInstanceField.get(null);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateClass(Class<T> clz) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        try {
            return clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //  Do nothing. Try using Unsafe instead.
        }

        return (T) getUnsafe().allocateInstance(clz);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(WireComponentInfo wireComponentInfo)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        if (!wireComponentInfo.hasConstructorInjection()) {
            return (T) instantiateClass(wireComponentInfo.getClz());
        }

        WireConstructorInjectionInfo constructorInjectionInfo = wireComponentInfo.getConstructorInjectionInfo();
        Constructor<?> constructor = constructorInjectionInfo.getConstructor();
        Object[] resolvedParameters = constructorInjectionInfo.getWiringParams()
                .stream()
                .map(this::getWireComponent)
                .toArray();
        return (T) constructor.newInstance(resolvedParameters);
    }

    @Override
    public <T> T createWireComponent(WireComponentInfo wireComponentInfo) {
        try {
            T instance = instantiate(wireComponentInfo);
            injectFields(instance, wireComponentInfo);
            injectSetters(instance, wireComponentInfo);
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException e) {
            throw new DIException(e);
        }
    }

    @Override
    public void assignWiringContext(WiringContext wiringContext) {
        this.context = wiringContext;
    }
}
