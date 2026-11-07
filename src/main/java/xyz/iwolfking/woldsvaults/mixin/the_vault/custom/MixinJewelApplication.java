package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.item.tool.JewelApplication;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.PrestigeTree;
import iskallia.vault.world.data.PlayerPrestigePowersData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.prestige.JewelSizePrestigePower;
import xyz.iwolfking.woldsvaults.util.ToolerPrestigeApplicationContext;

@Mixin(value = JewelApplication.class, remap = false)
public abstract class MixinJewelApplication {


    @Redirect(method = "plan", at = @At(value = "INVOKE", target = "Liskallia/vault/item/tool/ToolItem;applyJewel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Z)Z", remap = false), remap = false)
    private static boolean applyJewelWithToolerPrestigePower(ItemStack tool, ItemStack jewel, boolean checkCapacity) {
        Player player = ToolerPrestigeApplicationContext.getPlayer();
        if (player == null) {
            return ToolItem.applyJewel(tool, jewel, checkCapacity);
        }

        // Capacity-granting geodes must retain their benefit and TOOL_CAPACITY_ADDED bookkeeping.
        if (VaultGearData.read(jewel).getFirstValue(ModGearAttributes.JEWEL_SIZE).orElse(0) <= 0) {
            return ToolItem.applyJewel(tool, jewel, checkCapacity);
        }

        float freeCapacityChance = getFreeCapacityChance(player);
        boolean freeCapacity = freeCapacityChance > 0.0F && player.getRandom().nextFloat() < freeCapacityChance;

        VaultGearData toolData = VaultGearData.read(tool);
        int capacityBeforeApply = toolData.getFirstValue(ModGearAttributes.TOOL_CAPACITY).orElse(0);

        // A successful prestige roll skips only the capacity check. Restore the capacity
        // after application so the jewel's modifiers and all normal station behavior remain intact.
        boolean applied = ToolItem.applyJewel(tool, jewel, freeCapacity ? false : checkCapacity);
        if (applied && freeCapacity) {
            VaultGearData appliedToolData = VaultGearData.read(tool);
            appliedToolData.createOrReplaceAttributeValue(ModGearAttributes.TOOL_CAPACITY, capacityBeforeApply);
            appliedToolData.write(tool);
        }

        return applied;
    }

    private static float getFreeCapacityChance(Player player) {
        if (!(player.level instanceof ServerLevel serverLevel)) {
            return 0.0F;
        }

        PrestigeTree prestiges = PlayerPrestigePowersData.get(serverLevel).getPowers(player);
        if (prestiges == null) {
            return 0.0F;
        }

        float chance = 0.0F;
        for (JewelSizePrestigePower power : prestiges.getAll(JewelSizePrestigePower.class, Skill::isUnlocked)) {
            chance += power.getChance();
        }
        return Math.max(0.0F, Math.min(chance, 1.0F));
    }
}
