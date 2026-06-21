package xyz.iwolfking.woldsvaults.client.overworld;

import iskallia.vault.core.event.ClientEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverworldVaultCompassHandler {

    private static BlockPos OverworldVaultTarget = null;
    private static ResourceKey<Level> OverworldVaultTargetDimension = null;
    private static boolean initialized = false;

    public static void setTarget(BlockPos target) {
        setTarget(null, target);
    }

    public static void setTarget(ResourceKey<Level> dimension, BlockPos target) {
        OverworldVaultTargetDimension = dimension;
        OverworldVaultTarget = target;
    }


    public static void clearTarget() {
        OverworldVaultTargetDimension = null;
        OverworldVaultTarget = null;
    }

    public static BlockPos getTarget() {
        return OverworldVaultTarget;
    }

    public static boolean hasTargetFor(Level level) {
        return OverworldVaultTarget != null
                && (OverworldVaultTargetDimension == null || level.dimension().equals(OverworldVaultTargetDimension));
    }

    public static boolean shouldApplyOverworldVaultTarget(Level level) {
        return level != null && !level.dimension().location().getNamespace().equals("the_vault");
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        ClientEvents.COMPASS_PROPERTY.register(OverworldVaultCompassHandler.class, data -> {
            if (shouldApplyOverworldVaultTarget(data.getWorld()) && hasTargetFor(data.getWorld())) {
                data.setTarget(OverworldVaultTarget);
            }
        });
    }
}
