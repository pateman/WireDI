package pl.pateman.wiredi;

public interface WiringContext {
    <T> T getWireComponent(String wireName);
    <T> T getWireComponent(Class<T> clz);
}
