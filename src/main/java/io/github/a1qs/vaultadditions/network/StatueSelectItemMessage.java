package io.github.a1qs.vaultadditions.network;

import io.github.a1qs.vaultadditions.block.blockentity.LootStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StatueSelectItemMessage {
    public CompoundTag nbt;

    public static void encode(StatueSelectItemMessage message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.nbt);
    }

    public static StatueSelectItemMessage decode(FriendlyByteBuf buffer) {
        StatueSelectItemMessage message = new StatueSelectItemMessage();
        message.nbt = buffer.readNbt();
        return message;
    }

    public static void handle(StatueSelectItemMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {

            ItemStack stack = ItemStack.of(message.nbt.getCompound("Item"));
            BlockPos statuePos = NbtUtils.readBlockPos(message.nbt.getCompound("Position"));
            Level world = ctx.getSender().getLevel();
            BlockEntity te = world.getBlockEntity(statuePos);
            if (te instanceof LootStatueBlockEntity lootBlockEntity) {
                lootBlockEntity.setLootItem(stack);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static StatueSelectItemMessage selectItem(ItemStack stack, BlockPos statuePos) {
        StatueSelectItemMessage message = new StatueSelectItemMessage();
        message.nbt = new CompoundTag();
        message.nbt.put("Item", stack.serializeNBT());
        message.nbt.put("Position", NbtUtils.writeBlockPos(statuePos));
        return message;
    }

}
