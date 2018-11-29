package pl.pateman.wiredi.testcomponents.circular;

import pl.pateman.wiredi.Wire;
import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.WireName;

@WireComponent(name = "componentB")
public class ComponentB {

    private final ComponentA componentA;

    @Wire
    public ComponentB(@WireName("componentA") ComponentA componentA) {
        this.componentA = componentA;
    }
}
