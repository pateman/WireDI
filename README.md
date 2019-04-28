# WireDI

It's a very simple library which adds support for Dependency Injection to your application without bloating it too much (WireDI uses 100% pure Java reflection under the hood). If you have any prior experience with Spring, you'll like WireDI instantly.

## In a nutshell
Have a look at the following piece of code

```
@WireComponent(name = "someOtherComponent")
public class SomeOtherComponent
{
   private TestableComponent someComponent;
   private String test;

   @Wire
   public SomeOtherComponent(@WireName("another") TestableComponent someComponent, String test) {
      this.someComponent = someComponent;
      this.test = test;
   }

   @Wire
   public void setSomeComponent(@WireName("another") TestableComponent someComponent) {
      this.someComponent = someComponent;
   }
}
```

You annotate your components using `@WireComponent` and inject other components by using `@Wire`. There're three different 'strategies' supported and they're performed in the following order:

* Constructor injection
* Field injection
* Setter injection

In order to be able to use DI in your app, you need to create a `WiringContext`. Here's a simple example:

```
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
```

Examine the class `pl.pateman.wiredi.DefaultWiringContextIntegrationTest` to learn how to use the library. You can also have a look at other tests in the same package to see what other classes are available.

### Lifecycle methods
There's also support for two lifecycle methods - AfterInit and BeforeDestroy. Annotate a method (make sure not to use any parameters) with `@WireAfterInit` and `@WireBeforeDestroy` respectively. An AfterInit is called when a component is created by the context (after all its dependencies have been resolved) and a BeforeDestroy is called when the context's `destroy()` method is invoked.

### Injecting the context
`DefaultWireComponentFactory` supports injecting the current `WiringContext` into components. Just use the `WiringContext` type for your injected parameter/field and everything will happen automatically, for example:

```
@WireComponent
public class ComponentWithContextAsDependency {
    private final WiringContext context;

    @Wire
    public ComponentWithContextAsDependency(WiringContext context) {
        this.context = context;
    }

    public WiringContext getContext() {
        return context;
    }
}
```

## `@Wires`
Sometimes, you have classes in your application that you want to turn into wires, but cannot modify the source code to add the `@WireComponent` annotation. That's when the `@Wires` annotation comes into the mix. Create a new class, annotate it with this annotation and use static methods inside it to create wires (similar to Spring's `@Configuration`). Have a look at `pl.pateman.wiredi.testcomponents.Wirebox` to learn more.

## Dynamic wiring
It's also possible to dynamically add wires to the context. Dynamic wires are only injected using field injection. In order to mark a field for dynamic injection, use the `@Wire` annotation and set its `dynamic` attribute to `true`. The field will get set as soon as the wire becomes available.

Dynamic wiring works for both singleton and multi-instance components. Have a look at `pl.pateman.wiredi.DefaultWiringContextIntegrationTest.shouldWireDynamicWire` to learn more.

It's important to note that this feature should be used carefully, because it changes the state of instantiated components. Use with care.

## Documentation
Currently there's no documentation, sorry. Feel free to have a look at the code and tests to figure out what's going under the hood. Don't worry - the code is pretty straight-forward. :)

## Multithreading
There's one test which runs a simple scenario where 4 `Callable`s are invoked to get the very same singleton component. The test passes (i.e. only one is created) and frankly it's the only thing I've done to check whether the code is thread-safe, so use with care. I've tried to reduce the number of potentially unsafe pieces to minimum, but I can't fully assure you of thread-safety.

## Final words
Comments and questions are always welcome, so please contact me if you have any concerns/suggestions. 