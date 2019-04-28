package pl.pateman.wiredi.testcomponents.dynamic;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent(multiple = true)
public class ComponentWithDynamicWire {

    @Wire(name = "dynamicWire", dynamic = true)
    private DynamicWire wire;

    public DynamicWire getWire() {
        return wire;
    }
}
