package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent
public class HeavyInitComponent {

    public HeavyInitComponent() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            //  Do nothing.
        }
    }

}
