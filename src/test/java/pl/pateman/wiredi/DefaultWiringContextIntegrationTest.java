package pl.pateman.wiredi;

import org.junit.BeforeClass;
import org.junit.Test;
import pl.pateman.wiredi.core.DefaultWireComponentFactory;
import pl.pateman.wiredi.core.DefaultWiringContext;
import pl.pateman.wiredi.core.WireComponentInfoRegistry;
import pl.pateman.wiredi.core.WireComponentRegistry;
import pl.pateman.wiredi.exception.DIException;
import pl.pateman.wiredi.testcomponents.*;
import pl.pateman.wiredi.testcomponents.circular.ComponentB;
import pl.pateman.wiredi.testcomponents.dynamic.ComponentWithDynamicWire;
import pl.pateman.wiredi.testcomponents.dynamic.DynamicWire;
import pl.pateman.wiredi.testcomponents.dynamic.DynamicWireInterface;
import pl.pateman.wiredi.testcomponents.dynamic.SingletonComponentWithDynamicWire;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;
import pl.pateman.wiredi.util.PackageScanner;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class DefaultWiringContextIntegrationTest {

    private static List<Class<?>> scannedClasses;

    @BeforeClass
    public static void scanForClasses() {
        PackageScanner packageScanner = new PackageScanner();
        scannedClasses = packageScanner.getClasses("pl.pateman.wiredi.testcomponents");
    }

    private WiringContext givenContext() {
        WireComponentInfoRegistry wireComponentInfoResolver = new WireComponentInfoRegistry();
        DefaultWireComponentFactory wireComponentFactory = new DefaultWireComponentFactory();
        WireComponentRegistry wireComponentRegistry = new WireComponentRegistry();
        return new DefaultWiringContext(wireComponentInfoResolver, wireComponentFactory,
                wireComponentRegistry, scannedClasses);
    }

    private Callable<HeavyInitComponent> createCallable(final WiringContext wiringContext) {
        return new HeavyComponentCallable(wiringContext);
    }

    private <T> Optional<T> getFromFuture(Future<T> future) {
        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    @Test
    public void shouldInstantiateASimpleComponent() {
        WiringContext wiringContext = givenContext();

        Object wireComponent = wiringContext.getWireComponent(AlphanumericRandomStringGenerator.class);

        assertThat(wireComponent, instanceOf(AlphanumericRandomStringGenerator.class));
    }

    @Test(expected = DIException.class)
    public void shouldThrowAnExceptionWhenAComponentIsNotKnown() {
        WiringContext wiringContext = givenContext();

        wiringContext.getWireComponent("yadayada");
    }

    @Test(expected = DIException.class)
    public void shouldThrowAnExceptionWhenAComponentFailsToInitialize() {
        WiringContext wiringContext = givenContext();

        wiringContext.getWireComponent(FailingComponent.class);
    }

    @Test(expected = DIException.class)
    public void shouldThrowAnExceptionWhenCircularDependencyIsDetected() {
        WiringContext wiringContext = givenContext();

        wiringContext.getWireComponent(ComponentB.class);
    }

    @Test
    public void shouldCreateANewInstanceEveryTimeForMultipleComponents() {
        WiringContext wiringContext = givenContext();

        Set<Object> components = IntStream.range(0, 5)
                .mapToObj(i -> wiringContext.getWireComponent("lettersOnlyRandomStringGenerator"))
                .collect(Collectors.toSet());

        assertEquals(5, components.size());
    }

    @Test
    public void shouldNotCreateANewInstanceEveryTimeForSingletons() {
        WiringContext wiringContext = givenContext();

        Set<Object> components = IntStream.range(0, 5)
                .mapToObj(i -> wiringContext.getWireComponent("groupRegistry"))
                .collect(Collectors.toSet());

        assertEquals(1, components.size());
    }

    @Test
    public void shouldInitializeSingletonsSafelyInAMultithreadedEnv() {
        final WiringContext wiringContext = givenContext();
        final ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<Callable<HeavyInitComponent>> callables = IntStream.range(0, 4)
                .mapToObj(i -> createCallable(wiringContext))
                .collect(Collectors.toList());

        try {
            List<Future<HeavyInitComponent>> futures = executorService.invokeAll(callables);
            executorService.shutdown();

            Set<HeavyInitComponent> singletons = futures
                    .stream()
                    .map(this::getFromFuture)
                    .map(o -> o.orElseThrow(IllegalStateException::new))
                    .collect(Collectors.toSet());
            assertEquals(1, singletons.size());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRunBeforeDestroyWhenContextIsDestroyed() {
        WiringContext wiringContext = givenContext();

        Set<BeforeDestroyComponent> components = IntStream.range(0, 3)
                .mapToObj(i -> wiringContext.getWireComponent(BeforeDestroyComponent.class))
                .collect(Collectors.toSet());
        long beforeDestroy = components
                .stream()
                .map(BeforeDestroyComponent::isDestroyed)
                .filter(b -> !b)
                .count();
        wiringContext.destroy();
        long afterDestroy = components
                .stream()
                .map(BeforeDestroyComponent::isDestroyed)
                .filter(b -> !b)
                .count();

        assertEquals(3L, beforeDestroy);
        assertEquals(0L, afterDestroy);
    }

    @Test
    public void shouldReturnAllComponentsOfAGivenType() {
        WiringContext wiringContext = givenContext();

        Collection<RandomStringGenerator> generators = wiringContext.getWireComponentsOfType(RandomStringGenerator.class);

        assertEquals(2, generators.size());
        assertThat(generators, everyItem(instanceOf(RandomStringGenerator.class)));
    }

    @Test
    public void shouldInstantiateAComponentFromWires() {
        WiringContext wiringContext = givenContext();

        Wirebox.RequiresARandom requiresARandom = wiringContext.getWireComponent("requiresARandom");
        assertNotNull(requiresARandom);
        assertTrue(requiresARandom.hasRandom());
    }

    @Test
    public void shouldDynamicallyAddWire() {
        WiringContext wiringContext = givenContext();
        DynamicWire dynamicWire = new DynamicWire(12);

        wiringContext.addDynamicWire("dynamicWire", dynamicWire);
        Object instance1 = wiringContext.getWireComponent("dynamicWire");
        Object instance2 = wiringContext.getWireComponent(DynamicWireInterface.class);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(dynamicWire, instance1);
        assertEquals(dynamicWire, instance2);
    }

    @Test
    public void shouldLocateDynamicWireByType() {
        WiringContext wiringContext = givenContext();
        DynamicWire dynamicWire = new DynamicWire(10);

        wiringContext.addDynamicWire("wire", dynamicWire);
        Collection<DynamicWireInterface> wires = wiringContext.getWireComponentsOfType(DynamicWireInterface.class);

        assertThat(wires, hasItem(dynamicWire));
    }

    @Test
    public void shouldWireDynamicWire() {
        WiringContext wiringContext = givenContext();

        SingletonComponentWithDynamicWire singleton = wiringContext.getWireComponent(SingletonComponentWithDynamicWire.class);
        ComponentWithDynamicWire instance1 = wiringContext.getWireComponent(ComponentWithDynamicWire.class);
        assertNull(singleton.getField());
        assertNull(instance1.getWire());

        DynamicWire dynamicWire = new DynamicWire(1992);
        wiringContext.addDynamicWire("dynamicWire", dynamicWire);
        ComponentWithDynamicWire instance2 = wiringContext.getWireComponent(ComponentWithDynamicWire.class);

        assertNotNull(singleton.getField());
        assertNotNull(instance1.getWire());
        assertNotNull(instance2.getWire());
        assertEquals(1992, ((DynamicWire) singleton.getField()).getValue());
        assertEquals(dynamicWire, instance1.getWire());
        assertEquals(dynamicWire, instance2.getWire());
    }

    private class HeavyComponentCallable implements Callable<HeavyInitComponent> {

        private final WiringContext wiringContext;

        private HeavyComponentCallable(WiringContext wiringContext) {
            this.wiringContext = wiringContext;
        }

        @Override
        public HeavyInitComponent call() {
            return wiringContext.getWireComponent(HeavyInitComponent.class);
        }
    }
}
