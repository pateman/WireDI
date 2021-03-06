package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.core.DefaultWireComponentFactory;
import pl.pateman.wiredi.dto.*;
import pl.pateman.wiredi.testcomponents.ComponentWithContextAsDependency;
import pl.pateman.wiredi.testcomponents.RandomStringGenerator;
import pl.pateman.wiredi.testcomponents.Wirebox;
import pl.pateman.wiredi.testcomponents.dto.User;
import pl.pateman.wiredi.testcomponents.impl.AlphanumericRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.LettersOnlyRandomStringGenerator;
import pl.pateman.wiredi.testcomponents.impl.UserRegistryImpl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pl.pateman.wiredi.test.FieldValueInstanceOf.fieldValueInstanceOf;
import static pl.pateman.wiredi.test.HasAssignedFields.hasAssignedFields;

public class WireComponentFactoryTest {

    private WiringContext createWiringContext() {
        return new DummyWiringContext();
    }

    private DefaultWireComponentFactory givenFactory() {
        DefaultWireComponentFactory wireComponentFactory = new DefaultWireComponentFactory();
        wireComponentFactory.assignWiringContext(createWiringContext());
        return wireComponentFactory;
    }

    private WireComponentInfo givenUserDTOWireInfo() {
        WireComponentInfo wireComponentInfo = new WireComponentInfo(User.class, true);
        WireConstructorInjectionInfo constructorInjectionInfo = new WireConstructorInjectionInfo(User.class.getConstructors()[0]);
        constructorInjectionInfo.addWiringParam(String.class, null);
        constructorInjectionInfo.addWiringParam(String.class, null);
        wireComponentInfo.setConstructorInjectionInfo(constructorInjectionInfo);
        return wireComponentInfo;
    }

    private WireComponentInfo givenUserRegistryWireInfo() {
        WireComponentInfo wireComponentInfo = new WireComponentInfo(UserRegistryImpl.class, false);
        try {
            WireFieldInjectionInfo fieldInjectionInfo = new WireFieldInjectionInfo(UserRegistryImpl.class.getDeclaredField("firstNameGenerator"), "lettersOnlyRandomStringGenerator", false);
            WireSetterInjectionInfo setterInjectionInfo = new WireSetterInjectionInfo(UserRegistryImpl.class.getDeclaredMethod("setLastNameGenerator", RandomStringGenerator.class), "alphanumericRandomStringGenerator");
            wireComponentInfo.addFieldInjectionInfo(Collections.singletonList(fieldInjectionInfo));
            wireComponentInfo.addSetterInjectionInfo(Collections.singletonList(setterInjectionInfo));
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            //  Do nothing.
        }
        return wireComponentInfo;
    }

    private WireComponentInfo givenComponentWithContextWireInfo() {
        WireComponentInfo wireComponentInfo = new WireComponentInfo(ComponentWithContextAsDependency.class, false);
        WireConstructorInjectionInfo constructorInjectionInfo = new WireConstructorInjectionInfo(ComponentWithContextAsDependency.class.getConstructors()[0]);
        constructorInjectionInfo.addWiringParam(WiringContext.class, null);
        wireComponentInfo.setConstructorInjectionInfo(constructorInjectionInfo);
        return wireComponentInfo;
    }

    private WireComponentInfo givenComponentWithAfterInitWireInfo() {
        WireComponentInfo wireComponentInfo = new WireComponentInfo(ComponentWithAfterInit.class, true);
        try {
            Method afterInitMethod = ComponentWithAfterInit.class.getDeclaredMethod("initDone");
            WireLifecycleMethodsInfo wireLifecycleMethodsInfo = new WireLifecycleMethodsInfo(afterInitMethod, null);
            wireComponentInfo.setLifecycleMethodsInfo(wireLifecycleMethodsInfo);
        } catch (NoSuchMethodException e) {
            //  Do nothing.
        }
        return wireComponentInfo;
    }

    private WireComponentInfo givenComponentWithFactoryMethod() {
        try {
            return new WireComponentInfo(Random.class, false, Wirebox.class.getDeclaredMethod("someRandom"));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private WireComponentInfo givenComponentWithFactoryMethodAndParameters() {
        try {
            WireComponentInfo wireComponentInfo = new WireComponentInfo(Wirebox.RequiresARandom.class, true, Wirebox.class.getDeclaredMethod("requiresARandom", Random.class));
            wireComponentInfo.addFactoryMethodParam("java.util.Random");
            return wireComponentInfo;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Test
    public void shouldInstantiateATrivialComponent() {
        WireComponentInfo wireComponentInfo = new WireComponentInfo(TrivialComponent.class, false);
        DefaultWireComponentFactory wireComponentFactory = givenFactory();

        Object wireComponent = wireComponentFactory.createWireComponent(wireComponentInfo);

        assertThat(wireComponent, instanceOf(TrivialComponent.class));
    }

    @Test
    public void shouldInstantiateAComponentWithAConstructor() {
        WireComponentInfo wireComponentInfo = givenUserDTOWireInfo();
        DefaultWireComponentFactory wireComponentFactory = givenFactory();

        Object wireComponent = wireComponentFactory.createWireComponent(wireComponentInfo);

        assertThat(wireComponent, instanceOf(User.class));
        assertThat(((User) wireComponent).getFirstName(), nullValue());
        assertThat(((User) wireComponent).getLastName(), nullValue());
    }

    @Test
    public void shouldInstantiateAComponentAndInjectDependencies() {
        WireComponentInfo wireComponentInfo = givenUserRegistryWireInfo();
        DefaultWireComponentFactory componentFactory = givenFactory();

        Object wireComponent = componentFactory.createWireComponent(wireComponentInfo);

        assertThat(wireComponent, instanceOf(UserRegistryImpl.class));
        assertThat(wireComponent, hasAssignedFields("firstNameGenerator", "lastNameGenerator"));
    }

    @Test
    public void shouldInjectCurrentWiringContextAsDependency() {
        WireComponentInfo wireComponentInfo = givenComponentWithContextWireInfo();
        DefaultWireComponentFactory factory = givenFactory();

        Object wireComponent = factory.createWireComponent(wireComponentInfo);

        assertThat(wireComponent, instanceOf(ComponentWithContextAsDependency.class));
        assertThat(wireComponent, hasAssignedFields("context"));
        assertThat(wireComponent, fieldValueInstanceOf("context", DummyWiringContext.class));
    }

    @Test
    public void shouldExecuteAfterInit() {
        WireComponentInfo wireComponentInfo = givenComponentWithAfterInitWireInfo();
        DefaultWireComponentFactory factory = givenFactory();

        ComponentWithAfterInit wireComponent = factory.createWireComponent(wireComponentInfo);
        factory.invokeAfterInit(wireComponent, wireComponentInfo);

        assertEquals("test", wireComponent.getTest());
    }

    @Test
    public void shouldInstantiateAComponentUsingFactoryMethod() {
        WireComponentInfo wireComponentInfo = givenComponentWithFactoryMethod();
        DefaultWireComponentFactory factory = givenFactory();

        assertThat(factory.createWireComponent(wireComponentInfo), isA(Random.class));
    }

    @Test
    public void shouldInstantiateAComponentUsingFactoryMethodWithParameters() {
        WireComponentInfo wireComponentInfo = givenComponentWithFactoryMethodAndParameters();
        DefaultWireComponentFactory factory = givenFactory();

        Wirebox.RequiresARandom wireComponent = factory.createWireComponent(wireComponentInfo);
        assertTrue(wireComponent.hasRandom());
    }

    private class DummyWiringContext implements WiringContext {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getWireComponent(String wireName) {
            if ("alphanumericRandomStringGenerator".equals(wireName)) {
                return (T) new AlphanumericRandomStringGenerator();
            }
            if ("lettersOnlyRandomStringGenerator".equals(wireName)) {
                return (T) new LettersOnlyRandomStringGenerator();
            }
            if ("java.util.Random".equals(wireName)) {
                return (T) new Random();
            }
            return null;
        }

        @Override
        public <T> T getWireComponent(Class<T> clz) {
            return null;
        }

        @Override
        public void destroy() {
            //  Do nothing.
        }

        @Override
        public <T> Collection<T> getWireComponentsOfType(Class<T> clz) {
            return null;
        }

        @Override
        public void addDynamicWire(String wire, Object instance) {
            //  Do nothing.
        }
    }

    private class TrivialComponent {

    }

    private class ComponentWithAfterInit {
        private String test;

        private void initDone() {
            test = "test";
        }

        private String getTest() {
            return test;
        }
    }
}