package pl.pateman.wiredi;

public interface ComponentInfoResolver {
    WireComponentInfo getComponentInfo(Class<?> componentClass);
}
