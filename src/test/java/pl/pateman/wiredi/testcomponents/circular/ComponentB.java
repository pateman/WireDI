package pl.pateman.wiredi.testcomponents.circular;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.annotation.WireName;

@WireComponent(name = "componentB")
public class ComponentB {

    private final ComponentA componentA;

    @Wire
    public ComponentB(@WireName("componentA") ComponentA componentA) {
        this.componentA = componentA;
    }
}
