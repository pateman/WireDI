package pl.pateman.wiredi.testcomponents.dynamic;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent
public class SingletonComponentWithDynamicWire {

    @Wire(name = "dynamicWire", dynamic = true)
    private DynamicWireInterface field;

    public DynamicWireInterface getField() {
        return field;
    }
}
