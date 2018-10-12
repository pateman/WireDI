package pl.pateman.gunwo.di.test;

import pl.pateman.gunwo.di.WireComponent;

import javax.annotation.Resource;

@WireComponent(name = "someOtherComponent")
public class SomeOtherComponent
{
   @Resource
   private SomeComponent someComponent;
}
