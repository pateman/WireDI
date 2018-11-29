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
    WireComponentInfoResolver wireComponentInfoResolver = new WireComponentInfoResolver();
    DefaultWireComponentFactory wireComponentFactory = new DefaultWireComponentFactory();
    WireComponentRegistry wireComponentRegistry = new WireComponentRegistry();
    return new DefaultWiringContext(wireComponentInfoResolver, wireComponentFactory,
            wireComponentRegistry, scannedClasses);
}
```

Examine the class `pl.pateman.wiredi.DefaultWiringContextIntegrationTest` to learn how to use the library. You can also have a look at other tests in the same package to see what other classes are available.

## Documentation
Currently there's no documentation, sorry. Feel free to have a look at the code and tests to figure out what's going under the hood. Don't worry - the code is pretty straight-forward. :)

## Multithreading
There's one test which runs a simple scenario where 4 `Callable`s are invoked to get the very same singleton component. The test passes (i.e. only one is created) and frankly it's the only thing I've done to check whether the code is thread-safe, so use with care. I've tried to reduce the number of potentially unsafe pieces to minimum, but I can't fully assure you of thread-safety.

## Final words
Comments and questions are always welcome, so please contact me if you have any concerns/suggestions. 