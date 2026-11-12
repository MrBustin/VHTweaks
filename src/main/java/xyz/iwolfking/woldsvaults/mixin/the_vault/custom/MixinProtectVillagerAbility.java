package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerChallengeManager;

@Mixin(value = Ability.class, remap = false)
public abstract class MixinProtectVillagerAbility {
    @Inject(method = "onTick", at = @At("HEAD"))
    private void woldsvaults$stopBlockedActiveAbility(SkillContext context, CallbackInfo ci) {
        Ability ability = (Ability) (Object) this;
        if (ability.isActive()) {
            context.getSource().as(ServerPlayer.class).ifPresent(player -> {
                if (ProtectVillagerChallengeManager.isAbilityBlocked(player, ability.getClass().getSimpleName())) {
                    // Let the normal inactive tick clean up effects and keep cooldowns ticking.
                    ability.setActive(false);
                }
            });
        }
    }
}
