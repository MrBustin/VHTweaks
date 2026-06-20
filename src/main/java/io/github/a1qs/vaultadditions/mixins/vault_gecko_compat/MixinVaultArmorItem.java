package io.github.a1qs.vaultadditions.mixins.vault_gecko_compat;

import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.vault.gear.gecko.armor.GeckoArmorModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.armor.VaultGeckoArmorRenderer;
import iskallia.vault.item.gear.VaultArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mixin(VaultArmorItem.class)
public abstract class MixinVaultArmorItem extends DyeableArmorItem {
    private MixinVaultArmorItem() {
        super(null, null, null);
    }

    @Inject(at = @At("HEAD"), method = "getArmorTexture", cancellable = true, remap = false)
    public void getArmorTexture(ItemStack itemStack, Entity entity, EquipmentSlot slot, String type, CallbackInfoReturnable<String> cir) {
        if (entity != null && ModelUtil.getArmorModel(itemStack) instanceof GeckoArmorModel model
                && GeoArmorRenderer.getRenderer(getClass(), entity) instanceof VaultGeckoArmorRenderer<?>) {
            cir.setReturnValue(model.getTexturePath().toString());
        }
    }
}
