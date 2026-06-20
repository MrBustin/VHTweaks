package io.github.a1qs.vaultadditions.mixins.lucky_thorns;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.skill.talent.type.luckyhit.LuckyHitTalent;
import iskallia.vault.util.damage.AttackScaleHelper;
import iskallia.vault.util.damage.CritHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LuckyHitTalent.class, remap = false)
public class MixinLuckyHitTalent {
    @Redirect(method = "doLuckyHit", at = @At(value = "INVOKE", target = "Liskallia/vault/util/damage/CritHelper;getCrit(Lnet/minecraft/world/entity/player/Player;)Z"))
    private static boolean ignoreCritThorns(Player player, @Local(argsOnly = true) LivingHurtEvent event) {
        return CritHelper.getCrit(player) && !(event.getSource() instanceof ThornsReflectDamageSource);
    }

    @Redirect(method = "doLuckyHit", at = @At(value = "INVOKE", target = "Liskallia/vault/util/damage/AttackScaleHelper;getLastAttackScale(Lnet/minecraft/world/entity/player/Player;)F"))
    private static float ignoreScaleThorns(Player player, @Local(argsOnly = true) LivingHurtEvent event) {
        return event.getSource() instanceof ThornsReflectDamageSource ? 1.0F : AttackScaleHelper.getLastAttackScale(player);
    }
}
