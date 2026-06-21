package com.example.create_mobile_packages_addons.mixin;

import de.theidler.create_mobile_packages.robo.VirtualRobo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** VirtualRobo の private setSpeed(int) を呼び出すためのAccessor */
@Mixin(value = VirtualRobo.class, remap = false)
public interface VirtualRoboAccessor {

    @Invoker("setSpeed")
    void cmpa$invokeSetSpeed(int speed);
}
