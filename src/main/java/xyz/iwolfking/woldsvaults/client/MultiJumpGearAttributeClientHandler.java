package xyz.iwolfking.woldsvaults.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.api.helper.MultiJumpGearAttributeHelper;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.ServerboundMultiJumpMessage;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MultiJumpGearAttributeClientHandler {
    private static int clientJumpCount;
    private static boolean clientIsJumpHeld;
    private static boolean clientExtraJumpArmed;

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof LocalPlayer player) ||
                player != Minecraft.getInstance().player) {
            return;
        }

        if (player.getAbilities().flying || player.isSpectator()) {
            reset();
            return;
        }

        int maxExtraJumps = MultiJumpGearAttributeHelper.getExtraJumpCount(player);
        if (maxExtraJumps <= 0) {
            reset();
            return;
        }

        if (player.isOnGround()) {
            clientJumpCount = 0;
            clientIsJumpHeld = player.input.jumping;
            clientExtraJumpArmed = false;
            return;
        }

        if (!player.input.jumping) {
            clientIsJumpHeld = false;
            clientExtraJumpArmed = true;
            return;
        }

        if (!clientIsJumpHeld && clientExtraJumpArmed && clientJumpCount < maxExtraJumps) {
            clientJumpCount++;
            player.jumpFromGround();
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, movement.y + 0.2, movement.z);
            ModNetwork.sendToServer(new ServerboundMultiJumpMessage());
        }
        clientIsJumpHeld = true;
    }

    private static void reset() {
        clientJumpCount = 0;
        clientIsJumpHeld = false;
        clientExtraJumpArmed = false;
    }
}
