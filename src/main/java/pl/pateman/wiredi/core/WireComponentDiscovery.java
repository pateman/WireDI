package pl.pateman.wiredi.core;

import pl.pateman.wiredi.annotation.WireComponent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WireComponentDiscovery
{
   private WireComponentDiscovery() {

   }

   private static boolean isWireComponent(Class<?> clz) {
      return clz.isAnnotationPresent(WireComponent.class);
   }

   private static String getComponentName(Class<?> clz) {
      WireComponent annotation = clz.getAnnotation(WireComponent.class);
      String nameFromAnnotation = annotation.name();
      return nameFromAnnotation.isEmpty() ? clz.getCanonicalName() : nameFromAnnotation;
   }

   public static Map<String, Class<?>> findWireComponents(List<Class<?>> classes) {
      return classes
         .stream()
         .filter(WireComponentDiscovery::isWireComponent)
         .collect(Collectors.toMap(WireComponentDiscovery::getComponentName, Function.identity()));
   }
}
