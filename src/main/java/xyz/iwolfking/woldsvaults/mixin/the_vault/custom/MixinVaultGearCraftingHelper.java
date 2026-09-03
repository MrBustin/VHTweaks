package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.config.gear.VaultGearCraftingConfig;
import iskallia.vault.config.gear.VaultGearTypeConfig;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.PlayerProficiencyData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.configs.ServerConfigs;

import java.util.Random;

@Mixin(value = VaultGearCraftingHelper.class, remap = false)
public class MixinVaultGearCraftingHelper {
    @Shadow
    @Final
    private static Random rand;

    @Inject(method = "getCraftedRollType", at = @At("RETURN"), cancellable = true)
    private static void rollMythicCraft(ItemStack stack, ServerPlayer player, int level, CallbackInfoReturnable<VaultGearTypeConfig.RollType> cir) {
        double chance = ServerConfigs.MYTHIC_CRAFT_CHANCE.get();
        if (chance <= 0.0 || rand.nextDouble() >= chance) {
            return;
        }

        if (!isEligibleForMythicCraft(player, level)) {
            return;
        }

        ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool("Mythic").ifPresent(cir::setReturnValue);
    }

    private static boolean isEligibleForMythicCraft(ServerPlayer player, int craftedLevel) {
        if (player == null || craftedLevel < 100) {
            return false;
        }

        int playerLevel = iskallia.vault.util.SidedHelper.getVaultLevel(player);
        if (playerLevel < 100) {
            return false;
        }

        int absoluteProficiency = PlayerProficiencyData.get(player.getLevel()).getAbsoluteProficiency(player);
        int maxLevelProficiencyCap = VaultGearCraftingConfig.getProficiencyCap(100);
        return absoluteProficiency >= maxLevelProficiencyCap;
    }
}
