package pl.pateman.wiredi;

import java.util.Collection;

public interface WiringContext {
    <T> T getWireComponent(String wireName);
    <T> T getWireComponent(Class<T> clz);

    <T> Collection<T> getWireComponentsOfType(Class<T> clz);

    void destroy();
}
