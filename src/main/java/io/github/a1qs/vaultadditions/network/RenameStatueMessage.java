package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.block.blockentity.LootStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RenameStatueMessage {
    public CompoundTag nbt;


    public static void encode(RenameStatueMessage message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.nbt);
    }

    public static RenameStatueMessage decode(FriendlyByteBuf buffer) {
        RenameStatueMessage message = new RenameStatueMessage();
        message.nbt = buffer.readNbt();
        return message;
    }

    public static void handle(RenameStatueMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            CompoundTag data = message.nbt.getCompound("Data");
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                BlockPos pos = NbtUtils.readBlockPos(data.getCompound("Pos"));
                BlockEntity te = sender.getCommandSenderWorld().getBlockEntity(pos);
                if (te instanceof LootStatueBlockEntity lootBlockEntity) {
                    lootBlockEntity.getSkin().updateSkin(data.getString("PlayerNickname"));
                    lootBlockEntity.sendUpdates();
                }

            }
        });
        context.setPacketHandled(true);
    }

    public static RenameStatueMessage updateName(CompoundTag nbt) {
        RenameStatueMessage message = new RenameStatueMessage();
        message.nbt = nbt;
        return message;
    }
}
