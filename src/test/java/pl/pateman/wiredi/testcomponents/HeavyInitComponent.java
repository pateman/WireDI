package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.annotation.WireComponent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@WireComponent
public class HeavyInitComponent {

    private static final AtomicInteger NUM_INSTANCES = new AtomicInteger();

    private final int instanceNumber;

    public HeavyInitComponent() {
        instanceNumber = NUM_INSTANCES.getAndIncrement();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            //  Do nothing.
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeavyInitComponent that = (HeavyInitComponent) o;
        return instanceNumber == that.instanceNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceNumber);
    }
}
