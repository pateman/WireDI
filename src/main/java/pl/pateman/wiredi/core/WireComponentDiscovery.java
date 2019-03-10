package pl.pateman.wiredi.core;

import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.annotation.Wires;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public final class WireComponentDiscovery
{
   private WireComponentDiscovery() {

   }

   private static String getComponentName(Class<?> clz) {
      WireComponent annotation = clz.getAnnotation(WireComponent.class);
      String nameFromAnnotation = annotation.name();
      return nameFromAnnotation.isEmpty() ? clz.getCanonicalName() : nameFromAnnotation;
   }

    private static String getComponentName(Method method) {
        WireComponent annotation = method.getAnnotation(WireComponent.class);
        String nameFromAnnotation = annotation.name();
        return nameFromAnnotation.isEmpty() ? method.getName() : nameFromAnnotation;
    }

   public static Map<String, Class<?>> findWireComponents(List<Class<?>> classes) {
      return classes
         .stream()
              .filter(clz -> clz.isAnnotationPresent(WireComponent.class))
              .collect(toMap(WireComponentDiscovery::getComponentName, Function.identity()));
   }

    public static Map<String, Method> findWireComponentsInWires(List<Class<?>> classes) {
        return classes
                .stream()
                .filter(clz -> clz.isAnnotationPresent(Wires.class))
                .flatMap(clz -> Stream.of(clz.getDeclaredMethods()))
                .filter(mtd -> mtd.isAnnotationPresent(WireComponent.class))
                .collect(toMap(WireComponentDiscovery::getComponentName, Function.identity()));
   }
}
