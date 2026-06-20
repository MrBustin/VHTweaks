package io.github.a1qs.vaultadditions.mixins.wither_sound;

import io.github.a1qs.vaultadditions.config.ServerConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {
    private static final int WITHER_SPAWN_LEVEL_EVENT = 1023;
    private static final double WITHER_SPAWN_SOUND_RADIUS = 64.0D;

    @Inject(method = "globalLevelEvent", at = @At("HEAD"), cancellable = true)
    private void vaultadditions$limitWitherSpawnSound(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (!ServerConfigs.LIMIT_WITHER_SPAWN_GLOBAL_SOUND.get() || type != WITHER_SPAWN_LEVEL_EVENT) {
            return;
        }

        ServerLevel level = (ServerLevel)(Object)this;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        level.getServer().getPlayerList().broadcast(null, x, y, z, WITHER_SPAWN_SOUND_RADIUS, level.dimension(),
                new ClientboundSoundPacket(SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, x, y, z, 1.0F, 1.0F));
        ci.cancel();
    }
}
