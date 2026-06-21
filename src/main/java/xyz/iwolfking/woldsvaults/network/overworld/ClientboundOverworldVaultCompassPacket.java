package xyz.iwolfking.woldsvaults.network.overworld;

import xyz.iwolfking.woldsvaults.client.overworld.OverworldVaultCompassHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundOverworldVaultCompassPacket {
    private final boolean hasTarget;
    private final ResourceKey<Level> dimension;
    private final BlockPos target;

    public ClientboundOverworldVaultCompassPacket(ResourceKey<Level> dimension, BlockPos target) {
        this.hasTarget = target != null;
        this.dimension = dimension;
        this.target = target;
    }

    public static ClientboundOverworldVaultCompassPacket clear() {
        return new ClientboundOverworldVaultCompassPacket(null, null);
    }

    public static void encode(ClientboundOverworldVaultCompassPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.hasTarget);
        if (packet.hasTarget) {
            buf.writeResourceLocation(packet.dimension.location());
            buf.writeBlockPos(packet.target);
        }
    }

    public static ClientboundOverworldVaultCompassPacket decode(FriendlyByteBuf buf) {
        boolean hasTarget = buf.readBoolean();
        ResourceKey<Level> dimension = hasTarget
                ? ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation())
                : null;
        BlockPos target = hasTarget ? buf.readBlockPos() : null;
        return new ClientboundOverworldVaultCompassPacket(dimension, target);
    }

    public static void handle(ClientboundOverworldVaultCompassPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Must be handled on client thread
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (packet.hasTarget) {
                    OverworldVaultCompassHandler.setTarget(packet.dimension, packet.target);
                } else {
                    OverworldVaultCompassHandler.clearTarget();
                }
            });
        });
        context.setPacketHandled(true);
    }
}
