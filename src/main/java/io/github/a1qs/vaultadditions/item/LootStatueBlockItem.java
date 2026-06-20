package io.github.a1qs.vaultadditions.item;


import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.util.StatueLootSanitizer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class LootStatueBlockItem extends BlockItem {
    public LootStatueBlockItem(Block block) {
        super(block, (new Item.Properties()).tab(VaultAdditions.VAULT_ADDITIONS_TAB).stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> toolTip, TooltipFlag flagIn) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
            this.addStatueInformation(nbt.getCompound("BlockEntityTag"), toolTip);
        }

        super.appendHoverText(stack, worldIn, toolTip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addStatueInformation(CompoundTag dataTag, List<Component> toolTip) {
        if (dataTag.contains("PlayerNickname")) {
            String nickname = dataTag.getString("PlayerNickname");
            toolTip.add(new TextComponent("Player: "));
            toolTip.add((new TextComponent("- ")).append((new TextComponent(nickname)).withStyle(ChatFormatting.GOLD)));
        }

        Component itemDescriptor = (new TextComponent("NOT SELECTED")).withStyle(ChatFormatting.RED);
        if (dataTag.contains("LootItem")) {
            ItemStack lootItem = ItemStack.of(dataTag.getCompound("LootItem"));
            itemDescriptor = (new TextComponent(lootItem.getHoverName().getString())).withStyle(ChatFormatting.GREEN);
        }



        toolTip.add(TextComponent.EMPTY);
        toolTip.add(new TextComponent("Item: ").withStyle(ChatFormatting.WHITE));
        toolTip.add(new TextComponent("- ").append(itemDescriptor));

        if(dataTag.contains("TotalItems") && dataTag.contains("ItemsRemaining")) {
            toolTip.add(TextComponent.EMPTY);
            toolTip.add(new TextComponent("Status: ").withStyle(ChatFormatting.WHITE));
            if(dataTag.getInt("ItemsRemaining") <= 0) {
                toolTip.add(new TextComponent("- ").append(new TextComponent("☠ EXPIRED").withStyle(ChatFormatting.RED)));
            } else {
                toolTip.add(new TextComponent("- ").append(new TextComponent("✔ ACTIVE").withStyle(ChatFormatting.GREEN)));
            }
        }
    }

    public static ItemStack getStatueBlockItem(String nickname) {
        ItemStack itemStack = new ItemStack(ModBlocks.LOOT_STATUE_ITEM);

        CompoundTag nbt = new CompoundTag();
        nbt.putString("PlayerNickname", nickname);

        if (Configs.STATUE_LOOT_OMEGA != null) {
            ItemStack loot = StatueLootSanitizer.sanitizeOmegaLoot(Configs.STATUE_LOOT_OMEGA.randomLoot());
            nbt.put("LootItem", loot.serializeNBT());
        }

        CompoundTag stackNBT = new CompoundTag();
        stackNBT.put("BlockEntityTag", nbt);
        itemStack.setTag(stackNBT);
        return itemStack;
    }

    public static String getStatueName(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
            if(nbt.getCompound("BlockEntityTag").contains("PlayerNickname")) {
                return nbt.getCompound("BlockEntityTag").getString("PlayerNickname");
            }
        }
        return null;
    }

    public static void setStatueName(ItemStack stack, String name) {
        stack.getOrCreateTagElement("BlockEntityTag").putString("PlayerNickname", name);
    }

    public ItemStack getDefaultInstance() {
        return getStatueBlockItem("Steve");
    }

    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        return super.canPlace(pContext, pState);
    }
}
