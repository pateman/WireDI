package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.annotation.WireAfterInit;
import pl.pateman.wiredi.annotation.WireBeforeDestroy;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.core.WireComponentInfoRegistry;
import pl.pateman.wiredi.dto.WireComponentInfo;
import pl.pateman.wiredi.exception.DIException;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.Wirebox;
import pl.pateman.wiredi.testcomponents.dto.User;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.GroupRegistryImpl;
import pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class WireComponentInfoRegistryTest {

    private WireComponentInfoRegistry givenRegistry() {
        return new WireComponentInfoRegistry();
    }

    @Test(expected = DIException.class)
    public void shouldThrowWhenClassIsNotAWireComponent() {
        WireComponentInfoRegistry registry = givenRegistry();

        registry.getComponentInfo(User.class);
    }

    @Test
    public void shouldResolveWireInfoForComponentWithNoDependencies() {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(AlphanumericRandomStringGenerator.class);

        assertTrue(componentInfo.isMultipleAllowed());
        assertFalse(componentInfo.hasConstructorInjection());
        assertFalse(componentInfo.hasFieldInjectionInfo());
        assertFalse(componentInfo.hasSetterInjectionInfo());
    }

    @Test
    public void shouldResolveWireInfoForComponentWithConstructorInjection() {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(GroupRegistryImpl.class);

        assertFalse(componentInfo.isMultipleAllowed());
        assertTrue(componentInfo.hasConstructorInjection());
        List<Class<?>> constructorParams = Arrays.asList(componentInfo
                .getConstructorInjectionInfo()
                .getConstructor()
                .getParameterTypes());
        assertThat(constructorParams, hasItem(UserRegistry.class));
    }

    @Test
    public void shouldResolveWireInfoForComponentWithFieldAndSetterInjection() {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(UserRegistryImpl.class);

        assertFalse(componentInfo.isMultipleAllowed());
        assertFalse(componentInfo.hasConstructorInjection());
        assertTrue(componentInfo.hasFieldInjectionInfo());
        assertTrue(componentInfo.hasSetterInjectionInfo());
        assertEquals("lettersOnlyRandomStringGenerator", componentInfo
                .getFieldInjectionInfo()
                .get(0)
                .getWireName());
        assertEquals("alphanumericRandomStringGenerator", componentInfo
                .getSetterInjectionInfo()
                .get(0)
                .getWireName());
    }

    @Test
    public void shouldResolveWireInfoForComponentWithLifecycleMethods() {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(ComponentWithBeforeDestroy.class);

        assertTrue(componentInfo.hasLifecycleMethods());
        assertTrue(componentInfo.getLifecycleMethodsInfo().hasAfterInit());
        assertEquals("postConstruct", componentInfo.getLifecycleMethodsInfo().getAfterInitMethod().getName());
        assertTrue(componentInfo.getLifecycleMethodsInfo().hasBeforeDestroy());
        assertEquals("preDestroy", componentInfo.getLifecycleMethodsInfo().getBeforeDestroyMethod().getName());
    }

    @Test
    public void shouldResolveWireInfoForComponentInWires() throws NoSuchMethodException {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(Wirebox.class.getDeclaredMethod("someRandom"));
        assertTrue(componentInfo.hasFactoryMethod());
        assertNotNull(componentInfo.getFactoryMethod());
        assertEquals(Random.class, componentInfo.getClz());
        assertFalse(componentInfo.isMultipleAllowed());
    }

    @Test
    public void shouldResolveWireInfoWithParamsForComponentInWires() throws NoSuchMethodException {
        WireComponentInfoRegistry registry = givenRegistry();

        WireComponentInfo componentInfo = registry.getComponentInfo(Wirebox.class.getDeclaredMethod("requiresARandom", Random.class));
        assertTrue(componentInfo.hasFactoryMethod());
        assertNotNull(componentInfo.getFactoryMethod());
        assertEquals(Wirebox.RequiresARandom.class, componentInfo.getClz());
        assertTrue(componentInfo.isMultipleAllowed());
        assertEquals(1, componentInfo.getFactoryMethodParamsInfo().size());
        assertEquals("java.util.Random", componentInfo.getFactoryMethodParamsInfo().get(0));
    }

    @Test(expected = DIException.class)
    public void shouldNotResolveWireInfoForUnannotatedComponentInWires() throws NoSuchMethodException {
        WireComponentInfoRegistry registry = givenRegistry();

        registry.getComponentInfo(Wirebox.class.getMethod("thisShouldNotBeDetected"));
    }

    @Test
    public void shouldReturnFilteredComponentInfo() {
        WireComponentInfoRegistry registry = givenRegistry();

        registry.getComponentInfo(UserRegistryImpl.class);
        registry.getComponentInfo(GroupRegistryImpl.class);

        Collection<WireComponentInfo> componentsInfo = registry.getComponentsInfo(
                wci -> wci.getFieldInjectionInfo().stream().anyMatch(f -> "lettersOnlyRandomStringGenerator".equals(f.getWireName()))
        );
        assertEquals(1, componentsInfo.size());
        assertEquals(UserRegistryImpl.class, componentsInfo.iterator().next().getClz());
    }

    private class ComponentWithAfterInit {
        @WireAfterInit
        private void postConstruct() {
            //  Do nothing.
        }

        @WireAfterInit
        private void invalidPostConstruct(int someParam) {
            //  Do nothing.
        }
    }

    @WireComponent
    private class ComponentWithBeforeDestroy extends ComponentWithAfterInit {
        @WireBeforeDestroy
        private void preDestroy() {
            //  Do nothing.
        }
    }
}