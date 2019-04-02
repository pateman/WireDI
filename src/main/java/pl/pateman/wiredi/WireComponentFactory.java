package pl.pateman.wiredi;

import pl.pateman.wiredi.dto.WireComponentInfo;

public interface WireComponentFactory {
    <T> T createWireComponent(WireComponentInfo wireComponentInfo);

    void assignWiringContext(WiringContext wiringContext);

    void invokeAfterInit(Object componentInstance, WireComponentInfo wireComponentInfo);
}
