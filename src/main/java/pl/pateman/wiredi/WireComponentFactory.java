package pl.pateman.wiredi;

public interface WireComponentFactory {
    <T> T createWireComponent(WireComponentInfo wireComponentInfo);
    void assignWiringContext(WiringContext wiringContext);
}
