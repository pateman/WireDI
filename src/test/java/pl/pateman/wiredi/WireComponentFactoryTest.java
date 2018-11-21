package pl.pateman.wiredi;

public class WireComponentFactoryTest {

    private WiringContext createWiringContext() {
        return new DummyWiringContext();
    }

    private WireComponentFactory givenFactory() {
        WireComponentFactory wireComponentFactory = new WireComponentFactory();
        wireComponentFactory.setContext(createWiringContext());
        return wireComponentFactory;
    }

    private WireComponentInfo givenComponentInfo(Class<?> clz) {
        new WireComponentInfoResolver().
    }

    private class DummyWiringContext implements WiringContext {

        @Override
        public <T> T getWireComponent(String wireName) {
            return null;
        }

        @Override
        public <T> T getWireComponent(Class<T> clz) {
            return null;
        }
    }
}