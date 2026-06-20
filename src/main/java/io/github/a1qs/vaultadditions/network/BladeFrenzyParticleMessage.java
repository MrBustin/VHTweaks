package io.github.a1qs.vaultadditions.network;

import iskallia.vault.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class BladeFrenzyParticleMessage {
    private final Vec3 playerPos;
    private final float radius;

    public BladeFrenzyParticleMessage(Vec3 playerPos, float radius) {
        this.playerPos = playerPos;
        this.radius = radius;
    }

    public static void encode(BladeFrenzyParticleMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.playerPos.x);
        buffer.writeDouble(message.playerPos.y);
        buffer.writeDouble(message.playerPos.z);
        buffer.writeFloat(message.radius);
    }

    public static BladeFrenzyParticleMessage decode(FriendlyByteBuf buffer) {
        return new BladeFrenzyParticleMessage(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readFloat());
    }

    public static void handle(BladeFrenzyParticleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                spawnParticles(message.playerPos, message.radius);
            }

        });
        context.setPacketHandled(true);
    }

//    @OnlyIn(Dist.CLIENT)
//    public static void spawnParticles(Vec3 pos, float maxRadius) {
//        Level level = Minecraft.getInstance().level;
//        if (level != null) {
//            Random random = new Random();
//            ParticleEngine pm = Minecraft.getInstance().particleEngine;
//
//            int count = 150; // Total particles in each slash
//            float arcStart = -45.0F; // Starting angle of the arc relative to the base
//            float arcEnd = 45.0F;    // Ending angle of the arc relative to the base
//            float arcStep = (arcEnd - arcStart) / count; // Angle step per particle
//            int numSlashes = 4; // Number of slashes
//            float offsetAngle = 60.0F;
//            float slashOffsetDistance = 1.5F; // Distance offset for each slash from the origin
//
//            // Loop through the number of slashes
//            for (int slashIndex = 0; slashIndex < numSlashes; slashIndex++) {
//                float baseAngle = slashIndex * offsetAngle; // Calculate base angle for each slash
//
//                Vec3 slashOffset = new Vec3(
//                        slashOffsetDistance * Math.cos(Math.toRadians(baseAngle)),
//                        0, // Keep vertical position constant
//                        slashOffsetDistance * Math.sin(Math.toRadians(baseAngle))
//                );
//
//                // Apply the offset to the particle origin
//                Vec3 slashOrigin = pos.add(slashOffset);
//
//                for (int i = 0; i < count; i++) {
//                    float angle = baseAngle + arcStart + (arcStep * i); // Current angle in the arc
//                    float progress = (float) i / count; // Progress of the particle along the arc (0.0 to 1.0)
//                    float radius = maxRadius * progress; // Expanding radius as particles are spawned
//                    float speed = random.nextFloat() * 0.1F + 0.05F; // Speed variation
//                    float mix = random.nextFloat(); // For color blending
//
//                    // Offset for particle position based on angle and radius
//                    Vec3 offset = new Vec3(
//                            (radius * Math.cos(Math.toRadians(angle))),
//                            random.nextDouble() * 0.2F - 0.1F, // Slight vertical variation
//                            (radius * Math.sin(Math.toRadians(angle)))
//                    );
//
//                    double tangentialSpeed = speed;
//                    Vec3 velocity = new Vec3(
//                            -offset.z * tangentialSpeed, // Negative z for tangential motion
//                            random.nextDouble() * 0.05F, // Slight vertical variation
//                            offset.x * tangentialSpeed  // Positive x for tangential motion
//                    );
//
//                    // Particle creation
//                    Particle nova = pm.createParticle(
//                            ModParticles.NOVA_CLOUD.get(),
//                            slashOrigin.x() + offset.x,
//                            slashOrigin.y() + offset.y,
//                            slashOrigin.z() + offset.z,
//                            velocity.x,
//                            velocity.y,
//                            velocity.z
//                    );
//                    nova.setColor(
//                            Mth.lerp(mix, 0.8F, 0.5F), // Red: lighter blue
//                            Mth.lerp(mix, 0.9F, 0.7F), // Green: lighter blue
//                            Mth.lerp(mix, 1.0F, 1.0F)  // Blue: full intensity
//                    );
//                    nova.scale(1.5F + random.nextFloat() * 0.3F);
//
//                    // Optional: Additional particle effects for more depth
//                    Particle pylon = pm.createParticle(
//                            ParticleTypes.SWEEP_ATTACK,
//                            slashOrigin.x() + offset.x,
//                            slashOrigin.y() + offset.y,
//                            slashOrigin.z() + offset.z,
//                            velocity.x * 2.5, // Slightly faster for variation
//                            velocity.y,
//                            velocity.z * 2.5
//                    );
//                    pylon.setColor(
//                            Mth.lerp(mix, 0.8F, 0.5F), // Red
//                            Mth.lerp(mix, 0.9F, 0.7F), // Green
//                            Mth.lerp(mix, 1.0F, 1.0F)  // Blue
//                    );
//                    pylon.scale(2.0F + random.nextFloat() * 0.3F);
//                }
//            }
//        }
//    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnParticles(Vec3 pos, float maxRadius) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Random random = new Random();
            ParticleEngine pm = Minecraft.getInstance().particleEngine;

            int count = 150; // Total particles in the circular rotation
            float angleStep = 360.0F / count; // Angle step per particle
            int numRings = 2; // Number of rotating particle rings
            float ringHeight = 1.0F; // Vertical offset between rings

            // Get player view direction
            LocalPlayer player = Minecraft.getInstance().player;
            Vec3 viewDir = player.getLookAngle(); // Player's current facing direction

            // Generate circular rotating particles (NOVA_CLOUD)
            for (int ringIndex = 0; ringIndex < numRings; ringIndex++) {
                float heightOffset = ringIndex * ringHeight; // Vertical offset for this ring

                for (int i = 0; i < count; i++) {
                    // Calculate the angle for the current particle
                    float angle = i * angleStep; // Angle in degrees
                    float radius = maxRadius - 2.5F; // Fixed radius for the rotation
                    float speed = 0.2F; // Speed of particle rotation
                    float mix = random.nextFloat(); // For color blending

                    // Position of the particle in the circle
                    Vec3 offset = new Vec3(
                            radius * Math.cos(Math.toRadians(angle)),
                            heightOffset, // Add vertical offset for the ring
                            radius * Math.sin(Math.toRadians(angle))
                    );

                    // Velocity for circular rotation
                    Vec3 velocity = new Vec3(
                            -offset.z * speed, // Tangential velocity: perpendicular to radius
                            random.nextDouble() * 0.02F, // Slight vertical variation
                            offset.x * speed
                    );

                    // Particle creation
                    Particle nova = pm.createParticle(
                            ModParticles.WENDARR_SPARK_EXPLODE.get(),
                            pos.x() + offset.x,
                            pos.y() + offset.y,
                            pos.z() + offset.z,
                            velocity.x,
                            velocity.y,
                            velocity.z
                    );
                    nova.setColor(
                            Mth.lerp(mix, 1.0F, 0.8F), // Red: high intensity for warm colors
                            Mth.lerp(mix, 0.8F, 0.7F), // Green: medium to high intensity
                            Mth.lerp(mix, 0.3F, 0.1F)  // Blue: low intensity for orange/yellow, near 0 for warm
                    );
                    nova.scale(1.5F + random.nextFloat() * 0.3F);

                    Particle pylon = pm.createParticle(
                            ParticleTypes.CRIMSON_SPORE,
                            pos.x() + offset.x,
                            pos.y() + offset.y,
                            pos.z() + offset.z,
                            velocity.x * 2.5, // Slightly faster for variation
                            velocity.y,
                            velocity.z * 2.5
                    );
                    pylon.setColor(
                            Mth.lerp(mix, 0.8F, 1.0F), // Red
                            Mth.lerp(mix, 0.5F, 0.8F), // Green
                            Mth.lerp(mix, 0.5F, 1.0F)  // Blue
                    );
                    pylon.scale(1.5F + random.nextFloat() * 0.3F);

                    Particle other = pm.createParticle(
                            ParticleTypes.ENCHANT,
                            pos.x() + offset.x,
                            pos.y() + offset.y,
                            pos.z() + offset.z,
                            velocity.x * 2.5, // Slightly faster for variation
                            velocity.y,
                            velocity.z * 2.5
                    );
                    other.scale(0.5F + random.nextFloat() * 0.3F);
                }
            }

            // Generate directional burst particles (SWEEP_ATTACK)
            int sweepCount = 18; // Total SWEEP_ATTACK particles
            float sweepSpread = 180.0F; // Spread angle of the sweep particles
            float sweepSpeed = 1.0F; // Speed multiplier for SWEEP_ATTACK particles
            float sweepOffsetDistance = 10.0F; // Distance from the player to spawn the particles

            for (int i = 0; i < sweepCount; i++) {
                // Randomize the spread angle within the cone
                float yawOffset = (random.nextFloat() - 0.5F) * sweepSpread; // Horizontal spread

                // Flatten the view direction to remove vertical influence
                Vec3 horizontalViewDir = new Vec3(viewDir.x, 0, viewDir.z).normalize();

                // Adjust the flattened view direction to account for horizontal spread
                Vec3 adjustedDirection = new Vec3(
                        horizontalViewDir.x * Math.cos(Math.toRadians(yawOffset)) - horizontalViewDir.z * Math.sin(Math.toRadians(yawOffset)),
                        0, // Keep the particles on the horizontal plane
                        horizontalViewDir.z * Math.cos(Math.toRadians(yawOffset)) + horizontalViewDir.x * Math.sin(Math.toRadians(yawOffset))
                ).normalize();

                // Spawn position further along the adjusted horizontal direction
                Vec3 sweepSpawnPosition = pos.add(adjustedDirection.scale(sweepOffsetDistance));

                // Particle velocity in the adjusted direction
                Vec3 velocity = adjustedDirection.scale(sweepSpeed + random.nextDouble() * 0.2F);

                // Create the SWEEP_ATTACK particle
                Particle sweep = pm.createParticle(
                        ParticleTypes.SWEEP_ATTACK,
                        sweepSpawnPosition.x(),
                        sweepSpawnPosition.y() + 1.0F, // Slightly above the player for visibility
                        sweepSpawnPosition.z(),
                        velocity.x,
                        velocity.y,
                        velocity.z
                );
                sweep.scale(4.0F + random.nextFloat() * 0.5F); // Larger particles
            }
        }
    }



}
