package io.github.a1qs.vaultadditions.mixins.armor_effects.sound;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.vault.gear.effect.ElytraSoundTransmogEffect;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {

    @ModifyArg(method = "onSyncedDataUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private SoundInstance elytraSound(SoundInstance pSound) {
        ElytraSoundTransmogEffect effect = Configs.TRANSMOG_EFFECTS_CONFIG.getEffect(cast(), ElytraSoundTransmogEffect.class);
        return effect != null ? effect.createSoundInstance(cast()) : pSound;
    }

    @Unique
    private LocalPlayer cast() {
        return (LocalPlayer) (Object) this;
    }
}
