package io.github.a1qs.vaultadditions.client.sound;

import io.github.a1qs.vaultadditions.mixins.armor_effects.sound.AccessorAbstractSoundInstance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomElytraSoundInstance extends ElytraOnPlayerSoundInstance {
    private final float volumeModifier;

    public CustomElytraSoundInstance(LocalPlayer pPlayer, SoundEvent sound, float volumeModifier) {
        super(pPlayer);
        ((AccessorAbstractSoundInstance) this).setLocation(sound.getLocation());
        this.volumeModifier = volumeModifier;
    }

    public void tick() {
        super.tick();
        this.volume *= volumeModifier;
    }
}
