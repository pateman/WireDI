package pl.pateman.wiredi.testcomponents.circular;

import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.annotation.WireName;

@WireComponent(name = "componentA")
public class ComponentA {

    private final ComponentB componentB;

    @Wire
    public ComponentA(@WireName("componentB") ComponentB componentB) {
        this.componentB = componentB;
    }
}
