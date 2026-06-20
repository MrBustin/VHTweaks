package io.github.a1qs.vaultadditions.item;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import iskallia.vault.block.item.SoulPlaqueBlockItem;
import iskallia.vault.init.ModConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RaidPlaqueBlockItem extends SoulPlaqueBlockItem {
    public RaidPlaqueBlockItem(Block block) {
        super(block);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            if (getOwnerUUID(itemStack).isEmpty()) {
                setOwnerUUID(itemStack, player.getUUID());
                setOwnerName(itemStack, player.getName().getString());
            }

            int tier = Configs.RAID_PLAQUE_CONFIG.getTier(getScore(itemStack));
            if (getTier(itemStack).orElse(0) != tier) {
                setTier(itemStack, tier);
            }

            super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
        }
    }

    public static ItemStack create(UUID ownerUUID, String ownerNickname, int score) {
        ItemStack itemStack = new ItemStack(ModBlocks.RAID_PLAQUE.get());
        if (ownerUUID != null) {
            setOwnerUUID(itemStack, ownerUUID);
        }

        if (ownerNickname != null) {
            setOwnerName(itemStack, ownerNickname);
        }

        setTier(itemStack, ModConfigs.ASCENSION.getTier(score));
        setScore(itemStack, score);
        return itemStack;
    }
}
