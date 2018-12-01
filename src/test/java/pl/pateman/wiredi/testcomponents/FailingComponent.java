package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent
public class FailingComponent {

    public FailingComponent() {
        throw new IllegalStateException("You shall not pass!");
    }
}
