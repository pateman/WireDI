package pl.pateman.gunwo.di.test;

import pl.pateman.gunwo.di.Wire;
import pl.pateman.gunwo.di.WireComponent;

import javax.annotation.Resource;

@WireComponent(name = "someOtherComponent")
public class SomeOtherComponent
{
   private SomeComponent someComponent;

   public SomeOtherComponent() {
   }

   public SomeOtherComponent(SomeComponent someComponent) {
      this.someComponent = someComponent;
   }

   @Wire
   public void setSomeComponent(SomeComponent someComponent) {
      this.someComponent = someComponent;
   }
}
