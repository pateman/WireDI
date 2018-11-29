package pl.pateman.wiredi.testcomponents.circular;

import pl.pateman.wiredi.Wire;
import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.WireName;

@WireComponent(name = "componentA")
public class ComponentA {

    private final ComponentB componentB;

    @Wire
    public ComponentA(@WireName("componentB") ComponentB componentB) {
        this.componentB = componentB;
    }
}
