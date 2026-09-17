package xyz.iwolfking.woldsvaults.mixin.registry;

import com.google.common.collect.Table;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.custom.loot.LootTriggerAttribute;
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

import java.util.List;

@Mixin(value = ModGearAttributes.class, remap = false)
public class MixinModGearAttributes {
    @Shadow @Final private static Table<Attribute, AttributeModifier.Operation, VaultGearAttribute<?>> VANILLA_ATTRIBUTES;
    @Shadow @Final private static List<VaultGearAttribute<? extends LootTriggerAttribute>> LOOT_TRIGGER_ATTRIBUTES;

    @Inject(method = "registerAssociations", at = @At("TAIL"))
    private static void registerAdditionalAssociations(CallbackInfo ci) {
        VANILLA_ATTRIBUTES.put(ModAttributes.SIZE_SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL, xyz.iwolfking.woldsvaults.init.ModGearAttributes.SIZE_SCALE);
        VaultGearAttribute<? extends LootTriggerAttribute> percentManaPerLootedTile =
                xyz.iwolfking.woldsvaults.init.ModGearAttributes.PERCENT_MANA_PER_LOOTED_TILE;
        if (!LOOT_TRIGGER_ATTRIBUTES.contains(percentManaPerLootedTile)) {
            LOOT_TRIGGER_ATTRIBUTES.add(percentManaPerLootedTile);
        }
    }
}
