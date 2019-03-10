package pl.pateman.wiredi;

import pl.pateman.wiredi.dto.WireComponentInfo;

import java.lang.reflect.Method;

public interface ComponentInfoResolver {
    WireComponentInfo getComponentInfo(Class<?> componentClass);

    WireComponentInfo getComponentInfo(Method factoryMethod);
}
