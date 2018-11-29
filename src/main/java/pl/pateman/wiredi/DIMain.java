package pl.pateman.wiredi;

import pl.pateman.wiredi.test.SomeComponent;
import pl.pateman.wiredi.test.SomeOtherComponent;

import java.util.List;

public class DIMain
{
   public static void main(String[] args)
   {
      PackageScanner packageScanner = new PackageScanner();
      List<Class<?>> scannedClasses = packageScanner.getClasses("pl.pateman.wiredi");
      WireComponentInfoResolver wireComponentInfoResolver = new WireComponentInfoResolver();
      DefaultWireComponentFactory wireComponentFactory = new DefaultWireComponentFactory();
      WireComponentRegistry wireComponentRegistry = new WireComponentRegistry();
      WiringContext wiringContext = new DefaultWiringContext(wireComponentInfoResolver, wireComponentFactory,
              wireComponentRegistry, scannedClasses);
      SomeOtherComponent wireComponent1 = wiringContext.getWireComponent(SomeOtherComponent.class);
      SomeOtherComponent wireComponent2 = wiringContext.getWireComponent(SomeOtherComponent.class);

      SomeComponent inst1 = wiringContext.getWireComponent(SomeComponent.class);
      SomeComponent inst2 = wiringContext.getWireComponent(SomeComponent.class);

      String w = "";
   }
}
