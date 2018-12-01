package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.WiringContext;
import pl.pateman.wiredi.annotation.Wire;
import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent
public class ComponentWithContextAsDependency {
    private final WiringContext context;

    @Wire
    public ComponentWithContextAsDependency(WiringContext context) {
        this.context = context;
    }

    public WiringContext getContext() {
        return context;
    }
}
