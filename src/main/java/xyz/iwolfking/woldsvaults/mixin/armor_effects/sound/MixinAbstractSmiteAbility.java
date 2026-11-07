package xyz.iwolfking.woldsvaults.mixin.armor_effects.sound;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.gear.effect.AbilitySoundTransmogEffect;
import xyz.iwolfking.woldsvaults.util.SoundChoice;

@Mixin(AbstractSmiteAbility.class)
public abstract class MixinAbstractSmiteAbility {
    @Redirect(method = {"strike", "castExtraBolt"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1))
    private void woldsvaults$archonBoltSound(Level level, Player excluded, double x, double y, double z,
                                           SoundEvent event, SoundSource source, float volume, float pitch,
                                           @Local(argsOnly = true) ServerPlayer caster) {
        SoundChoice sound = AbilitySoundTransmogEffect.getSound(caster, "Smite_Abstract",
                new SoundChoice(event, 0.4F, pitch));
        level.playSound(excluded, x, y, z, sound.event(), source, sound.volume(), sound.pitch());
    }
}