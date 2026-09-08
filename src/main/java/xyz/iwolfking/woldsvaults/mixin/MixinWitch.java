package xyz.iwolfking.woldsvaults.mixin;

import iskallia.vault.world.data.PlayerImmunityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Witch.class)
public class MixinWitch {

    @Redirect(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 1))
    private boolean checkForPoisonImmunity(LivingEntity entity, MobEffect pEffect) {
        if (!(entity instanceof Player p)) {
            return !entity.hasEffect(pEffect);
        }
        return !new PlayerImmunityData().getEffects(p.getUUID()).contains(pEffect) && !entity.hasEffect(pEffect);
    }
}
