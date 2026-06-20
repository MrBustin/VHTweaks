package io.github.a1qs.vaultadditions.client.render;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientElytraFlyingEvent {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (player.level.isClientSide() && player.isFallFlying() && ModelUtil.isWearingJetpackPiece(player)) {
            double playerX = player.getX();
            double playerY = player.getY();
            double playerZ = player.getZ();

            float yaw = player.getYRot();
            float pitch = player.getXRot();

            double exhaustOffsetHorizontal = 0.25; // Distance from the player's center to the sides
            double exhaustOffsetVertical = 0.5;  // Slightly above the player
            double depthOffset = -0.1;            // Slightly behind the player


            if(Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                exhaustOffsetVertical -= 0.3;
                depthOffset -= 2.5;
            }

            double yawRad = Math.toRadians(yaw);
            double pitchRad = Math.toRadians(pitch);

            double behindX = -Math.sin(yawRad) * depthOffset;
            double behindZ = Math.cos(yawRad) * depthOffset;

            double leftExhaustX = playerX + behindX - Math.cos(yawRad) * exhaustOffsetHorizontal;
            double leftExhaustY = playerY + exhaustOffsetVertical - Math.sin(pitchRad) * depthOffset;
            double leftExhaustZ = playerZ + behindZ - Math.sin(yawRad) * exhaustOffsetHorizontal;
            spawnJetpackParticles(player.level, leftExhaustX, leftExhaustY, leftExhaustZ);

            double rightExhaustX = playerX + behindX + Math.cos(yawRad) * exhaustOffsetHorizontal;
            double rightExhaustY = playerY + exhaustOffsetVertical - Math.sin(pitchRad) * depthOffset;
            double rightExhaustZ = playerZ + behindZ + Math.sin(yawRad) * exhaustOffsetHorizontal;
            spawnJetpackParticles(player.level, rightExhaustX, rightExhaustY, rightExhaustZ);
        }
    }

    private static void spawnJetpackParticles(Level world, double x, double y, double z) {
        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, -0.0, 0); // Add some smoke for extra realism
    }




}
