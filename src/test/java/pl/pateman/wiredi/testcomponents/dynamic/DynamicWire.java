package pl.pateman.wiredi.testcomponents.dynamic;

import java.util.Objects;

public class DynamicWire implements DynamicWireInterface {

    private final int value;

    public DynamicWire(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicWire that = (DynamicWire) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public void doSomething() {
        //  Do nothing.
    }
}
