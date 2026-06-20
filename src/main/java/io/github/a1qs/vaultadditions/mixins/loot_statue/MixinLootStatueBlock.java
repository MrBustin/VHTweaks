package io.github.a1qs.vaultadditions.mixins.loot_statue;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.util.StatueLootSanitizer;
import iskallia.vault.block.LootStatueBlock;
import iskallia.vault.block.LootStatueUpperBlock;
import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.container.LootStatueContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(value = LootStatueBlock.class)
public class MixinLootStatueBlock {

    /**
     * @author a1qs
     * @reason loot statue fix for not selecting items after being placed with a pre-set name
     */
    @Overwrite
    public void setPlacedBy(Level pLevel, BlockPos pos, BlockState pState, LivingEntity pPlacer, ItemStack stack) {
        BlockPos blockpos = pos.above();
        CompoundTag tag = stack.getTag();
        pLevel.setBlock(blockpos, ModBlocks.LOOT_STATUE_UPPER.defaultBlockState().setValue(LootStatueUpperBlock.HALF, Half.BOTTOM), 3);
        pLevel.setBlock(blockpos.above(), ModBlocks.LOOT_STATUE_UPPER.defaultBlockState().setValue(LootStatueUpperBlock.HALF, Half.TOP), 3);
        if (!(pPlacer instanceof ServerPlayer player) || !(pLevel.getBlockEntity(pos) instanceof LootStatueTileEntity be) || tag == null) {
            return;
        }

        if(!tag.getCompound("BlockEntityTag").contains("LootItem")) {
            CompoundTag data = new CompoundTag();
            ListTag itemList = new ListTag();

            List<ItemStack> options = Configs.STATUE_LOOT_OMEGA.getOptions();
            for (ItemStack option : options) {
                itemList.add(StatueLootSanitizer.sanitizeOmegaLoot(option).serializeNBT());
            }

            data.put("Items", itemList);
            data.put("Position", NbtUtils.writeBlockPos(pos));
            NetworkHooks.openGui(player, new MenuProvider() {
                public @NotNull Component getDisplayName() {
                    return new TextComponent("Loot Statue Options");
                }

                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new LootStatueContainer(windowId, data);
                }
            }, (buffer) -> buffer.writeNbt(data));
        }

        ItemStack lootItem = be.getLootItem();
        ItemStack sanitizedLoot = StatueLootSanitizer.sanitizeOmegaLoot(lootItem);
        if (!ItemStack.isSame(lootItem, sanitizedLoot) || !ItemStack.tagMatches(lootItem, sanitizedLoot) || lootItem.getCount() != sanitizedLoot.getCount()) {
            be.setLootItem(sanitizedLoot);
            lootItem = sanitizedLoot;
        }
        CompoundTag lootTag = lootItem.getTag();
        if (tag.getCompound("BlockEntityTag").contains("LootItem") && lootTag != null) {
            lootTag.remove("Charged");
            if (lootTag.isEmpty()) {
                lootItem.setTag(null);
            }
        }
    }
}
