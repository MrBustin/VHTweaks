package xyz.iwolfking.woldsvaults.client.events;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.init.additions.ModKeybinds;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.LeaderboardRequestMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, value = Dist.CLIENT)
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
