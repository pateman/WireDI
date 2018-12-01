package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.annotation.WireAfterInit;
import pl.pateman.wiredi.annotation.WireBeforeDestroy;
import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.core.WireComponentInfoResolver;
import pl.pateman.wiredi.dto.WireComponentInfo;
import pl.pateman.wiredi.exception.DIException;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.dto.User;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.GroupRegistryImpl;
import pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class WireComponentInfoResolverTest {

    private WireComponentInfoResolver givenResolver() {
        return new WireComponentInfoResolver();
    }

    @Test(expected = DIException.class)
    public void shouldThrowWhenClassIsNotAWireComponent() {
        WireComponentInfoResolver resolver = givenResolver();

        resolver.getComponentInfo(User.class);
    }

    @Test
    public void shouldResolveWireInfoForComponentWithNoDependencies() {
        WireComponentInfoResolver resolver = givenResolver();

        WireComponentInfo componentInfo = resolver.getComponentInfo(AlphanumericRandomStringGenerator.class);

        assertTrue(componentInfo.isMultipleAllowed());
        assertFalse(componentInfo.hasConstructorInjection());
        assertFalse(componentInfo.hasFieldInjectionInfo());
        assertFalse(componentInfo.hasSetterInjectionInfo());
    }

    @Test
    public void shouldResolveWireInfoForComponentWithConstructorInjection() {
        WireComponentInfoResolver resolver = givenResolver();

        WireComponentInfo componentInfo = resolver.getComponentInfo(GroupRegistryImpl.class);

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
        WireComponentInfoResolver resolver = givenResolver();

        WireComponentInfo componentInfo = resolver.getComponentInfo(UserRegistryImpl.class);

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
        WireComponentInfoResolver resolver = givenResolver();

        WireComponentInfo componentInfo = resolver.getComponentInfo(ComponentWithBeforeDestroy.class);

        assertTrue(componentInfo.hasLifecycleMethods());
        assertTrue(componentInfo.getLifecycleMethodsInfo().hasAfterInit());
        assertEquals("postConstruct", componentInfo.getLifecycleMethodsInfo().getAfterInitMethod().getName());
        assertTrue(componentInfo.getLifecycleMethodsInfo().hasBeforeDestroy());
        assertEquals("preDestroy", componentInfo.getLifecycleMethodsInfo().getBeforeDestroyMethod().getName());
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