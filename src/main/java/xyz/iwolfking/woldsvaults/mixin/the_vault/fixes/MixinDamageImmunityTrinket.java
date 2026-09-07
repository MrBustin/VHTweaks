package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.DamageImmunityTrinket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DamageImmunityTrinket.class, remap = false)
public class MixinDamageImmunityTrinket {
    @Inject(method = "onPotionEffect", at = @At("HEAD"), remap = false)
    private static void woldsvaults$carapaceBlocksExtraEffect(
            PotionEvent.PotionApplicableEvent event,
            CallbackInfo ci
    ) {
        LivingEntity entity = event.getEntityLiving();

        if (!(entity instanceof Player player)) {
            return;
        }

        MobEffect effect = event.getPotionEffect().getEffect();

        if (effect != MobEffects.LEVITATION) {
            return;
        }

        boolean hasCarapace = TrinketHelper.getTrinkets(player, DamageImmunityTrinket.class)
                .stream()
                .anyMatch(stack -> {
                    DamageImmunityTrinket trinket = (DamageImmunityTrinket) stack.trinket();
                    return trinket.getRegistryName() != null
                            && "carapace".equals(trinket.getRegistryName().getPath());
                });

        if (hasCarapace) {
            event.setResult(Event.Result.DENY);
        }
    }
}
