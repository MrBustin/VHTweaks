package xyz.iwolfking.woldsvaults.mixin.the_vault;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearLegendaryHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.api.gear.modification.WoldGearModifierHelper;
import xyz.iwolfking.woldsvaults.api.util.PrestigePowerHelper;
import xyz.iwolfking.woldsvaults.expertises.CraftsmanExpertise;
import xyz.iwolfking.woldsvaults.expertises.EclecticGearExpertise;

import java.util.List;
import java.util.Random;

@Mixin(value = GearRollHelper.class, remap = false)
public class MixinGearRollHelper {
    @Shadow @Final public static Random rand;

    @Inject(method = "canGenerateLegendaryModifier", at = @At(value = "TAIL"), cancellable = true)
    private static void canGenerateLegendaryModifier(Player player, VaultGearData data, CallbackInfoReturnable<Boolean> cir) {
        if(data.equals(VaultGearData.empty())) {
            return;
        }

        if ((Boolean) data.getFirstValue(ModGearAttributes.CRAFTED_BY).isPresent() && rand.nextFloat() < ModConfigs.VAULT_GEAR_COMMON.getLegendaryModifierChance()) {
            ExpertiseTree expertises = PlayerExpertisesData.get((ServerLevel) player.getLevel()).getExpertises(player);
            int craftsmanLevel = 0;

            for (CraftsmanExpertise craftsmanExpertise : expertises.getAll(CraftsmanExpertise.class, Skill::isUnlocked)) {
                craftsmanLevel = craftsmanExpertise.getCraftsmanLevel();
            }
            if(craftsmanLevel > 0) {
                cir.setReturnValue(true);
            }

        }

        if(data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE_POOL).isPresent()) {
            if(data.getFirstValue(ModGearAttributes.GEAR_ROLL_TYPE_POOL).get().equals("jewel_crafted") && rand.nextFloat() < 0.02F) {
                cir.setReturnValue(true);
            }
        }
    }

//    @Inject(method = "initializeGear(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Liskallia/vault/gear/VaultGearModifierHelper;generateAffixSlots(Lnet/minecraft/world/item/ItemStack;Ljava/util/Random;)V"))
//    private static void initializeGearWithCraftingPotential(ItemStack stack, Player player, CallbackInfo ci) {
//        if(player instanceof ServerPlayer serverPlayer) {
//            List<CraftingPotentialPrestigePower> craftingPotentialPrestigePowers = PrestigePowerHelper.getPrestigePowersOfType(serverPlayer, CraftingPotentialPrestigePower.class);
//            if(craftingPotentialPrestigePowers.isEmpty()) {
//                return;
//            }
//
//            int additionalPotential = 0;
//
//            for(CraftingPotentialPrestigePower power : craftingPotentialPrestigePowers) {
//                additionalPotential += power.getPotentialIncrease();
//            }
//
//            if(additionalPotential > 0) {
//                VaultGearData data = VaultGearData.read(stack);
//                int currentPotential = data.getFirstValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL).orElse(0);
//                data.createOrReplaceAttributeValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL, currentPotential + additionalPotential);
//                data.createOrReplaceAttributeValue(ModGearAttributes.CRAFTING_POTENTIAL, currentPotential + additionalPotential);
//                data.write(stack);
//            }
//        }
//    }

    @Inject(method = "initializeGear(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Liskallia/vault/gear/VaultGearModifierHelper;generateModifiers(Lnet/minecraft/world/item/ItemStack;Ljava/util/Random;)Liskallia/vault/gear/modification/GearModification$Result;", shift = At.Shift.AFTER))
    private static void initializeGearWithEffects(ItemStack stack, Player player, CallbackInfo ci, @Local VaultGearData data) {

        int itemLevel = data.getItemLevel();
        float increasedSpecialRollsChance = 0.0F;

        if(player != null) {
            ExpertiseTree expertises = PlayerExpertisesData.get((ServerLevel) player.getLevel()).getExpertises(player);
            for (EclecticGearExpertise eclecticGearExpertise : expertises.getAll(EclecticGearExpertise.class, Skill::isUnlocked)) {
                increasedSpecialRollsChance += eclecticGearExpertise.getIncreasedChance();
            }
        }

        if (itemLevel >= 20 && rand.nextFloat() <= 0.12F + increasedSpecialRollsChance) { //12% is a test value, can change later
            long gameTime;
            if (player != null) {
                gameTime = player.level.getGameTime();
            } else {
                MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
                gameTime = currentServer != null ? currentServer.overworld().getGameTime() : 0L;
            }
            WoldGearModifierHelper.addUnusualModifier(stack, gameTime, rand);
        }
    }
}
