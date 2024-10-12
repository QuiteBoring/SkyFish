package org.skyfish.mixin.entity;

import net.minecraft.entity.projectile.EntityFishHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFishHook.class)
public interface EntityFishHookAccessor {

    @Accessor
    boolean getInGround();

}