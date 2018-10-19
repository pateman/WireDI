package pl.pateman.gunwo.di;

import pl.pateman.gunwo.di.test.SomeOtherComponent;

import java.util.List;

public class DIMain
{
   public static void main(String[] args)
   {
      PackageScanner packageScanner = new PackageScanner();
      List<Class<?>> scannedClasses = packageScanner.getClasses("pl.pateman.gunwo.di");
      WireComponentInfoResolver wireComponentInfoResolver = new WireComponentInfoResolver();
      WireComponentFactory wireComponentFactory = new WireComponentFactory();
      WiringContext wiringContext = new WiringContext(wireComponentInfoResolver, wireComponentFactory, scannedClasses);
      SomeOtherComponent wireComponent = wiringContext.getWireComponent(SomeOtherComponent.class);
      String w = "";
   }
}
