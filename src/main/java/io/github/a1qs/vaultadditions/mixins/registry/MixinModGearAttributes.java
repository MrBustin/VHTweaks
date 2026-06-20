package io.github.a1qs.vaultadditions.mixins.registry;

import com.google.common.collect.Table;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModGearAttributes.class, remap = false)
public class MixinModGearAttributes {
    @Shadow @Final private static Table<Attribute, AttributeModifier.Operation, VaultGearAttribute<?>> VANILLA_ATTRIBUTES;

    @Inject(method = "registerAssociations", at = @At("TAIL"))
    private static void registerAdditionalAssociations(CallbackInfo ci) {
        VANILLA_ATTRIBUTES.put(ModAttributes.SIZE_SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL, io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.SIZE_SCALE);
    }
}
