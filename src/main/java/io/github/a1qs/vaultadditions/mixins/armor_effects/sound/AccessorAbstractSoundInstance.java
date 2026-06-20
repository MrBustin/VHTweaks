package io.github.a1qs.vaultadditions.mixins.armor_effects.sound;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AccessorAbstractSoundInstance {
    @Mutable @Accessor()
    void setLocation(ResourceLocation location);
}
