package io.github.a1qs.vaultadditions.mixins.armor_effects.sound;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.util.SoundChoice;
import io.github.a1qs.vaultadditions.vault.gear.effect.AbilitySoundTransmogEffect;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ManaShieldAbility.class, remap = false)
public class MixinManaShieldAbility extends Ability {
    @Inject(method = "lambda$doAction$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    public void injectSoundEvents(ServerPlayer player, CallbackInfoReturnable<Ability.ActionResult> cir) {
        if (player == null) {
            VaultAdditions.LOGGER.error("Player is null when trying to play sound for 'Mana Shield'!");
            return;
        }
        SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.MANA_SHIELD, new SoundChoice(ModSounds.MANA_SHIELD, 0.2F, 0.2F));
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
    }

    @Inject(method = "on", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;removeModifier(Ljava/util/UUID;)V"))
    private static void modifyHitSound(LivingDamageEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SoundChoice def = new SoundChoice(ModSounds.MANA_SHIELD_HIT, 0.1F,  0.7F + player.getLevel().getRandom().nextFloat(0.6F));
            SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.MANA_SHIELD, 1, def);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
        }
    }

    @Redirect(method = "lambda$doAction$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void redirectSoundEvents(Level instance, Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float v4, float v5) {}

    @Redirect(method = "on", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void redirectSoundEvent(Level instance, Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float v4, float v5) {}

}
