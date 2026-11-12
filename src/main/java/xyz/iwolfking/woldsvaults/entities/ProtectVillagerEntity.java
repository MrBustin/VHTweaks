package xyz.iwolfking.woldsvaults.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.WoldsVaults;

/** A villager used as the protected target in the challenge that cannot be picked up. */
@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID)
public class ProtectVillagerEntity extends Villager {
    public ProtectVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventEasyVillagersPickup(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof ProtectVillagerEntity && event.getEntity().isCrouching()) {
            event.setCanceled(true);
        }
    }
}
