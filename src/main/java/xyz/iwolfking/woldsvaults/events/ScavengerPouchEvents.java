package xyz.iwolfking.woldsvaults.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.items.ItemScavengerPouch;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public final class ScavengerPouchEvents {
    private ScavengerPouchEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack stack = event.getItem().getItem();
        if (!ItemScavengerPouch.interceptPlayerInventoryItemAddition(inventory, stack)) {
            return;
        }

        event.getItem().setItem(ItemStack.EMPTY);
        event.setCanceled(true);
        player.level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.ITEM_PICKUP,
            SoundSource.PLAYERS,
            0.2F,
            (player.level.random.nextFloat() - player.level.random.nextFloat()) * 1.4F + 2.0F
        );
    }
}
