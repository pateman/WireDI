package pl.pateman.gunwo.di;

import java.util.List;
import java.util.Map;

public class DIMain
{
   public static void main(String[] args)
   {
      PackageScanner packageScanner = new PackageScanner();
      List<Class<?>> scannedClasses = packageScanner.getClasses("pl.pateman.gunwo.di");
      Map<String, Class<?>> wireComponents = WireComponentDiscovery.findWireComponents(scannedClasses);
      System.out.println(wireComponents);
   }
}
