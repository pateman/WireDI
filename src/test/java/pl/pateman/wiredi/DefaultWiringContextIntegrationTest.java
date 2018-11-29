package pl.pateman.wiredi;

import org.junit.BeforeClass;
import org.junit.Test;
import pl.pateman.wiredi.testcomponents.FailingComponent;
import pl.pateman.wiredi.testcomponents.HeavyInitComponent;
import pl.pateman.wiredi.testcomponents.circular.ComponentB;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultWiringContextIntegrationTest {

    private static List<Class<?>> scannedClasses;

    @BeforeClass
    public static void scanForClasses() {
        PackageScanner packageScanner = new PackageScanner();
        scannedClasses = packageScanner.getClasses("pl.pateman.wiredi.testcomponents");
    }

    private WiringContext givenContext() {
        WireComponentInfoResolver wireComponentInfoResolver = new WireComponentInfoResolver();
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

    private class HeavyComponentCallable implements Callable<HeavyInitComponent> {

        private final WiringContext wiringContext;

        private HeavyComponentCallable(WiringContext wiringContext) {
            this.wiringContext = wiringContext;
        }

        @Override
        public HeavyInitComponent call() throws Exception {
            return wiringContext.getWireComponent(HeavyInitComponent.class);
        }
    }
}