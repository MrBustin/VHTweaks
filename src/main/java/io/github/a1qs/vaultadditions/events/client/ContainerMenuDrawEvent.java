package io.github.a1qs.vaultadditions.events.client;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.client.menu.PlayerTraderMenu;
import io.github.a1qs.vaultadditions.container.slot.GhostSlot;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ContainerMenuDrawEvent {

    @SubscribeEvent
    public static void drawGhostSlot(ContainerScreenEvent.DrawForeground event) {
        if(event.getContainerScreen() instanceof PlayerTraderMenu menu) { // ultra specific but idc
            for(Slot slot : menu.getMenu().slots) {
                if(slot instanceof GhostSlot && slot.hasItem()) {
                    RenderSystem.disableDepthTest();
                    GuiComponent.fill(event.getPoseStack(),
                            slot.x,
                            slot.y,
                            slot.x + 16,
                            slot.y + 16,
                            0x88FFFFF0);

                    RenderSystem.enableDepthTest();
                }
            }
        }
    }
}
