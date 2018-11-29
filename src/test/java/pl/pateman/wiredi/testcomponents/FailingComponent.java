package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.WireComponent;

@WireComponent
public class FailingComponent {

    public FailingComponent() {
        throw new IllegalStateException("You shall not pass!");
    }
}
