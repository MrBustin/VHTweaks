package io.github.a1qs.vaultadditions.mixins.armor_effects.sound;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.util.SoundChoice;
import io.github.a1qs.vaultadditions.vault.gear.effect.AbilitySoundTransmogEffect;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.ability.effect.spi.core.ToggleManaAbility;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSmiteAbility.class, remap = false)
public class MixinAbstractSmiteAbility extends ToggleManaAbility {

    @Inject(method = "doDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1))
    public void injectSoundEvents(ServerPlayer player, float radius, float percentAbilityPowerDealt, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity entity) {
        if (player == null) {
            VaultAdditions.LOGGER.error("Player is null when trying to play sound for 'Smite: Archon'!");
            return;
        }
        SoundChoice def = new SoundChoice(ModSounds.SMITE_BOLT, 0.4F, 1.5F + Mth.randomBetween(entity.getRandom(), -0.2F, 0.2F));
        SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, "Smite_Abstract", def);
        player.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
    }

    @WrapWithCondition(method = "doDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1))
    private boolean redirectSoundEvents(Level instance, Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float v4, float v5) {
        return false;
    }
}
