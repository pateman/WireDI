package pl.pateman.wiredi;

import org.junit.Test;
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
}