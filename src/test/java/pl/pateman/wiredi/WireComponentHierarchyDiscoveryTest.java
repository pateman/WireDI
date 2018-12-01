package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.core.WireComponentHierarchyDiscovery;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.LettersOnlyRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WireComponentHierarchyDiscoveryTest {

    private Map<String, Class<?>> givenUserRegistryImplClassesMap() {
        return Collections.singletonMap("UserRegistry", UserRegistryImpl.class);
    }

    private Map<String, Class<?>> givenRandomGeneratorClassesMap() {
        Map<String, Class<?>> map = new HashMap<>();

        map.put("alpha", AlphanumericRandomStringGenerator.class);
        map.put("lettersOnly", LettersOnlyRandomStringGenerator.class);

        return map;
    }

    @Test
    public void shouldFindInterfaceAsCoreAncestor() {
        final String givenClassName = "pl.pateman.wiredi.testcomponents.UserRegistry";
        Map<String, Class<?>> classMap = givenUserRegistryImplClassesMap();

        Map<String, Set<String>> hierarchy = WireComponentHierarchyDiscovery.findHierarchy(classMap);

        assertTrue(hierarchy.containsKey(givenClassName));
        assertThat(hierarchy.get(givenClassName), hasItem("UserRegistry"));
    }

    @Test
    public void shouldFindBothInterfaceAndAbstractClassAsCoreAncestors() {
        Map<String, Class<?>> classMap = givenRandomGeneratorClassesMap();

        Map<String, Set<String>> hierarchy = WireComponentHierarchyDiscovery.findHierarchy(classMap);

        assertTrue(hierarchy.containsKey("pl.pateman.wiredi.testcomponents.impl.AbstractRandomStringGenerator"));
        assertTrue(hierarchy.containsKey("pl.pateman.wiredi.testcomponents.RandomStringGenerator"));
    }

    @Test
    public void shouldQualifyBothComponentsAsBothAbstractClassesAndInterfaces() {
        Map<String, Class<?>> classMap = givenRandomGeneratorClassesMap();

        Map<String, Set<String>> hierarchy = WireComponentHierarchyDiscovery.findHierarchy(classMap);
        Set<String> abstractClassComponents = hierarchy.get("pl.pateman.wiredi.testcomponents.impl.AbstractRandomStringGenerator");
        Set<String> interfaceComponents = hierarchy.get("pl.pateman.wiredi.testcomponents.RandomStringGenerator");

        assertThat(abstractClassComponents, hasItems("alpha", "lettersOnly"));
        assertThat(interfaceComponents, hasItems("lettersOnly", "alpha"));
    }

}