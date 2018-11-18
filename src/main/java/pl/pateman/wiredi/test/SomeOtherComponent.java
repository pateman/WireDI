package pl.pateman.wiredi.test;

import pl.pateman.wiredi.Wire;
import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.WireName;

@WireComponent(name = "someOtherComponent")
public class SomeOtherComponent
{
   private TestableComponent someComponent;

   public SomeOtherComponent() {
   }

   public SomeOtherComponent(TestableComponent someComponent) {
      this.someComponent = someComponent;
   }

   @Wire
   public void setSomeComponent(@WireName("another") TestableComponent someComponent) {
      this.someComponent = someComponent;
   }
}
