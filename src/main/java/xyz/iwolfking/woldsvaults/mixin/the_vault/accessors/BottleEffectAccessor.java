package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.item.bottle.BottleEffect;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BottleEffect.class, remap = false)
public interface BottleEffectAccessor {
    @Invoker("trigger")
    void callTrigger(ServerPlayer player);
}
