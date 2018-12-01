package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.annotation.WireBeforeDestroy;
import pl.pateman.wiredi.annotation.WireComponent;

@WireComponent(multiple = true)
public class BeforeDestroyComponent {

    private boolean destroyed;

    @WireBeforeDestroy
    private void beforeDie() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
