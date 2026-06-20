package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.block.blockentity.PlayerTraderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdatePlayerTraderDataMessage {
    private final BlockPos pos;
    private final String traderName;

    public UpdatePlayerTraderDataMessage(BlockPos pos, String traderName) {
        this.pos = pos;
        this.traderName = traderName;
    }


    public static void encode(UpdatePlayerTraderDataMessage msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.traderName, 16);
    }

    public static UpdatePlayerTraderDataMessage decode(FriendlyByteBuf buffer) {
        return new UpdatePlayerTraderDataMessage(buffer.readBlockPos(), buffer.readUtf(16));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender() != null) {
                ServerPlayer serverPlayer = context.get().getSender();
                ServerLevel level = serverPlayer.getLevel();


                if (level.getBlockEntity(pos) instanceof PlayerTraderBlockEntity playerTraderBlockEntity) {
                    if(playerTraderBlockEntity.getOwner().equals(serverPlayer.getUUID())) {
                        playerTraderBlockEntity.setTraderName(traderName);
                        playerTraderBlockEntity.updateBlock();
                        context.get().setPacketHandled(true);
                    }
                    VaultAdditions.LOGGER.warn("Player {} attempted to send a packet to modify PlayerTrader on position {}", serverPlayer.getName().getString(), pos);
                }
            }
        });
    }
}
