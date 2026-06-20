package io.github.a1qs.vaultadditions.mixins.vault_gecko_compat;

import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.vault.gear.gecko.armor.GeckoArmorModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.armor.VaultGeckoArmorRenderer;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mixin(VaultArmorRenderProperties.class)
public class MixinVaultArmorRenderProperties {
    @Inject(at = @At("HEAD"), method = "getArmorModel", cancellable = true, remap = false)
    public void getArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default, CallbackInfoReturnable<HumanoidModel<?>> cir) {
        if (entity != null && ModelUtil.getArmorModel(itemStack) instanceof GeckoArmorModel
                && GeoArmorRenderer.getRenderer((Class<ArmorItem>) itemStack.getItem().getClass(), entity) instanceof VaultGeckoArmorRenderer<?> renderer) {
            cir.setReturnValue((HumanoidModel<?>) renderer.applyEntityStats(_default)
                    .setCurrentItem(entity, itemStack, armorSlot)
                    .applySlot(armorSlot));
        }
    }
}
