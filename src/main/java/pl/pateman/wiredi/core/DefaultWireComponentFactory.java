package pl.pateman.wiredi.core;

import pl.pateman.wiredi.WireComponentFactory;
import pl.pateman.wiredi.WiringContext;
import pl.pateman.wiredi.dto.WireComponentInfo;
import pl.pateman.wiredi.dto.WireConstructorInjectionInfo;
import pl.pateman.wiredi.dto.WireFieldInjectionInfo;
import pl.pateman.wiredi.dto.WireSetterInjectionInfo;
import pl.pateman.wiredi.exception.DIException;
import pl.pateman.wiredi.util.PrimitiveDefaults;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class DefaultWireComponentFactory implements WireComponentFactory {
    private WiringContext context;

    private boolean isWireNameAWiringContext(String wireName) {
        return WiringContext.class.getCanonicalName().equals(wireName) || DefaultWiringContext.class.getCanonicalName().equals(wireName);
    }

    private Object getWireComponent(String wireName) {
        if (PrimitiveDefaults.isPrimitiveOrJavaType(wireName)) {
            return PrimitiveDefaults.getDefault(wireName);
        }
        if (context == null) {
            throw new DIException("DefaultWireComponentFactory requires a valid WiringContext");
        }

        if (isWireNameAWiringContext(wireName)) {
            return context;
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

    private void runAfterInitMethod(Object instance, WireComponentInfo wireComponentInfo) throws InvocationTargetException, IllegalAccessException {
        if (!wireComponentInfo.hasLifecycleMethods()) {
            return;
        }

        if (!wireComponentInfo.getLifecycleMethodsInfo().hasAfterInit()) {
            return;
        }

        Method afterInitMethod = wireComponentInfo.getLifecycleMethodsInfo().getAfterInitMethod();
        afterInitMethod.setAccessible(true);
        afterInitMethod.invoke(instance);
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
            return clz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            //  Do nothing. Try using Unsafe instead.
            return (T) getUnsafe().allocateInstance(clz);
        } catch (Exception ex) {
            throw new DIException("Error in initializer", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateFromFactoryMethod(WireComponentInfo wireComponentInfo) throws InvocationTargetException, IllegalAccessException {
        Method method = wireComponentInfo.getFactoryMethod();
        method.setAccessible(true);

        List<String> factoryMethodParams = wireComponentInfo.getFactoryMethodParamsInfo();
        if (!factoryMethodParams.isEmpty()) {
            int numberOfParams = factoryMethodParams.size();
            Object[] params = new Object[numberOfParams];

            for (int i = 0; i < numberOfParams; i++) {
                params[i] = getWireComponent(factoryMethodParams.get(i));
            }
            return (T) method.invoke(null, params);
        }

        return (T) method.invoke(null);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(WireComponentInfo wireComponentInfo)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        if (wireComponentInfo.hasFactoryMethod()) {
            return instantiateFromFactoryMethod(wireComponentInfo);
        }

        if (!wireComponentInfo.hasConstructorInjection()) {
            return (T) instantiateClass(wireComponentInfo.getClz());
        }

        WireConstructorInjectionInfo constructorInjectionInfo = wireComponentInfo.getConstructorInjectionInfo();
        Constructor<?> constructor = constructorInjectionInfo.getConstructor();
        Object[] resolvedParameters = constructorInjectionInfo.getWiringParams()
                .stream()
                .map(this::getWireComponent)
                .toArray();
        constructor.setAccessible(true);
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
        } catch (Exception ex) {
            throw new DIException("Exception during initialization", ex);
        }
    }

    @Override
    public void assignWiringContext(WiringContext wiringContext) {
        this.context = wiringContext;
    }

    @Override
    public void invokeAfterInit(Object componentInstance, WireComponentInfo wireComponentInfo) {
        if (componentInstance == null || wireComponentInfo == null) {
            throw new IllegalArgumentException("Both an instance and a WireComponentInfo are required");
        }

        try {
            runAfterInitMethod(componentInstance, wireComponentInfo);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DIException(e);
        }
    }
}
