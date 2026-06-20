package io.github.a1qs.vaultadditions.mixins.power_crystals.border;

import iskallia.vault.world.data.VirtualWorlds;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VirtualWorlds.class, remap = false)
public class MixinVirtualWorlds {
    // Ignore the WorldBorder Listener set for the Vault :3
    @Redirect(method = "load(Liskallia/vault/core/world/storage/VirtualWorld;)Liskallia/vault/core/world/storage/VirtualWorld;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;addListener(Lnet/minecraft/world/level/border/BorderChangeListener;)V"), remap = false)
    private static void redirectListener(WorldBorder instance, BorderChangeListener pListener) {
    }
}
