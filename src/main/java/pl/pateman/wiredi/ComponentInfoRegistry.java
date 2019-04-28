package pl.pateman.wiredi;

import pl.pateman.wiredi.dto.WireComponentInfo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Predicate;

public interface ComponentInfoRegistry {
    WireComponentInfo getComponentInfo(Class<?> componentClass);

    WireComponentInfo getComponentInfo(Method factoryMethod);

    WireComponentInfo addDynamicWireComponentInfo(Class<?> componentClass);

    Collection<WireComponentInfo> getComponentsInfo(Predicate<WireComponentInfo> predicate);
}
