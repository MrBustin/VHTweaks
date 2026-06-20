package io.github.a1qs.vaultadditions.events;

import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.MultiJumpTrinket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEntityFallEvent {

    /**
     * Handles the reduction/cancellation of fall damage for players with the Feather Trinket equipped.
     * <p>
     * If the player has an active and usable {@code MultiJumpTrinket}:
     * - Fall damage is canceled if the fall distance is less than 5.0 blocks to prevent damage ticking when taking no damage.
     * - Otherwise, the fall distance is reduced by 2.0 blocks to mitigate fall damage.
     * <p>
     * Called whenever a LivingEntity falls
     */
    @SubscribeEvent
    public static void multiJumpTrinketFallReductionEvent(LivingFallEvent event) {
        if(event.getEntityLiving() instanceof ServerPlayer player) {
            if (!TrinketHelper.getTrinkets(player, MultiJumpTrinket.class).stream().noneMatch((trinket) -> trinket.isUsable(player))) {
                if(event.getDistance() < 5.0F) {
                    event.setCanceled(true);
                } else {
                    event.setDistance(event.getDistance() - 2.0F);
                }
            }
        }
    }
}
