package pl.pateman.wiredi.test;

import pl.pateman.wiredi.Wire;
import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.WireName;

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
