package pl.pateman.wiredi;

import pl.pateman.wiredi.dto.WireComponentInfo;

public interface ComponentInfoResolver {
    WireComponentInfo getComponentInfo(Class<?> componentClass);
}
