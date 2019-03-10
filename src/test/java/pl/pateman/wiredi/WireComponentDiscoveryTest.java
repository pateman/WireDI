package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.core.WireComponentDiscovery;
import pl.pateman.wiredi.testcomponents.UserRegistry;
import pl.pateman.wiredi.testcomponents.Wirebox;
import pl.pateman.wiredi.testcomponents.dto.User;
import pl.pateman.wiredi.testcomponents.impl.GroupRegistryImpl;
import pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class WireComponentDiscoveryTest {

    @Test
    public void shouldProperlyDiscoverComponents() {
        List<Class<?>> classes = Arrays.asList(User.class, UserRegistry.class, UserRegistryImpl.class);

        Map<String, Class<?>> wireComponents = WireComponentDiscovery.findWireComponents(classes);

        assertEquals(1, wireComponents.size());
        assertThat(wireComponents.values(), hasItem(UserRegistryImpl.class));
    }

    @Test
    public void shouldDetermineCorrectNamesForDiscoveredComponents() {
        List<Class<?>> classes = Arrays.asList(UserRegistryImpl.class, GroupRegistryImpl.class);

        Map<String, Class<?>> wireComponents = WireComponentDiscovery.findWireComponents(classes);

        assertThat(wireComponents.keySet(), allOf(
                hasItem("groupRegistry"),
                hasItem("pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl")));
    }

    @Test
    public void shouldProperlyDiscoverComponentsInWires() {
        List<Class<?>> classes = Collections.singletonList(Wirebox.class);

        Map<String, Method> wireComponentsInWires = WireComponentDiscovery.findWireComponentsInWires(classes);

        assertEquals(2, wireComponentsInWires.size());
    }

    @Test
    public void shouldProperlyDetermineNamesForComponentsInWires() {
        List<Class<?>> classes = Collections.singletonList(Wirebox.class);

        Map<String, Method> wireComponentsInWires = WireComponentDiscovery.findWireComponentsInWires(classes);

        assertThat(wireComponentsInWires.keySet(), hasItems("someRandom", "dateFormatter"));
    }

}