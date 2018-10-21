package pl.pateman.gunwo.di.test;

import pl.pateman.gunwo.di.Wire;
import pl.pateman.gunwo.di.WireComponent;

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
   public void setSomeComponent(TestableComponent someComponent) {
      this.someComponent = someComponent;
   }
}
