package xyz.iwolfking.woldsvaults.mixin.the_vault.skills.modifications;

import iskallia.vault.block.entity.AbilitySuppressorTileEntity;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.etching.EtchingGearAttributes;
import iskallia.vault.gear.etching.EtchingHelper;
import iskallia.vault.skill.ability.effect.FireballAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.util.ServerScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.modifiers.gear.special.FireballModification;

@Mixin(value = FireballAbility.class, remap = false)
public abstract class MixinFireballAbility {
    @Shadow private static void shootTrinity(ServerPlayer player, float share) { throw new AssertionError(); }
    @Shadow private static void shootFireball(ServerPlayer player, float yaw, float share) { throw new AssertionError(); }

    @Inject(method = "lambda$doAction$0", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", remap = true))
    private static void woldsvaults$repeatCast(ServerPlayer player, CallbackInfoReturnable<Ability.ActionResult> cir) {
        for (var mod : SpecialAbilityModification.getModifications(player, FireballModification.class)) {
            if (player.level.random.nextFloat() <= mod.value().getValue()) {
                ServerScheduler.INSTANCE.scheduleForPlayer(player, 40, () -> {
                    if (AbilitySuppressorTileEntity.hasAbilitySuppressorAround(player)) return;
                    var trinity = EtchingHelper.getBestEtchingValue(player, EtchingGearAttributes.FIREBALL_SPLIT);
                    if (trinity.isPresent()) shootTrinity(player, trinity.get());
                    else shootFireball(player, 0.0F, 1.0F);
                    player.level.playSound((Player) null, player, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                });
            }
        }
    }
}