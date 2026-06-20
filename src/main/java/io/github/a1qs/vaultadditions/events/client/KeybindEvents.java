package io.github.a1qs.vaultadditions.events.client;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModKeybinds;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.LeaderboardRequestMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, value = Dist.CLIENT)
public class KeybindEvents {
    public static boolean isZephyrToggled = false;


    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {

        if(ModKeybinds.openLeaderboard.consumeClick()) {
            if(Minecraft.getInstance().player != null) {
                ModNetwork.sendToServer(new LeaderboardRequestMessage());
            }
        }

        if(ModKeybinds.toggleZephyr.consumeClick()) {
            if(Minecraft.getInstance().player != null) {
                isZephyrToggled = !isZephyrToggled;
                ChatFormatting color = isZephyrToggled ? ChatFormatting.RED : ChatFormatting.GREEN;
                String toggleText = isZephyrToggled ? "OFF" : "ON";
                Minecraft.getInstance().player.displayClientMessage(new TextComponent("Toggled Zephyr's Grace ").append(new TextComponent(toggleText).withStyle(color)), true);
            }
        }


    }
}
