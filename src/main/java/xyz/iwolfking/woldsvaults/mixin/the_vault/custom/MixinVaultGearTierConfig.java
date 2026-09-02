package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = VaultGearTierConfig.class, remap = false, priority = 1500)
public abstract class MixinVaultGearTierConfig {
    @Shadow
    static Optional<VaultGearTierConfig> getConfig(ResourceLocation key) {
        return null;
    }
    @Inject(method = "getConfig(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private static void getTierConfig(ItemStack stack, CallbackInfoReturnable<Optional<VaultGearTierConfig>> cir) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            VaultGearRarity rarity = data.getRarity();
            if (rarity.equals(VaultGearRarity.valueOf("MYTHIC"))) {
                cir.setReturnValue(getConfig(VaultMod.id(stack.getItem().getRegistryName().getPath() + "_mythic")));
            }
        }
    }
}
