package io.github.a1qs.vaultadditions.mixins.loot_statue;

import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.config.StatueLootConfig;
import iskallia.vault.config.entry.SingleItemEntry;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = StatueLootConfig.class, remap = false)
public class MixinStatueLootConfig {

    /**
     * @author a1qs
     * @reason I DONT CARE, PLEASE FIX THE F***** TAGS APPEARING ON STATUES
     */
    @Overwrite
    private ItemStack getItem(SingleItemEntry entry) {
        ItemStack stack = ItemStack.EMPTY;
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.ITEM));
            stack = new ItemStack(item);
            if (entry.NBT != null && !entry.NBT.equals("{}")) {
                stack.setTag(TagParser.parseTag(entry.NBT));
            }
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Error Getting StatueLootConfig Item", e);
        }
        return stack;
    }
}
