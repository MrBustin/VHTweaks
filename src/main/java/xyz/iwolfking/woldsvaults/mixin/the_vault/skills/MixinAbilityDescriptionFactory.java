package xyz.iwolfking.woldsvaults.mixin.the_vault.skills;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.component.AbilityDescriptionFactory;
import iskallia.vault.skill.ability.component.AbilityLabelBindingRegistry;
import iskallia.vault.skill.ability.component.AbilityLabelContext;
import iskallia.vault.skill.ability.component.AbilityLabelFactory;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

@Mixin(value = AbilityDescriptionFactory.class, remap = false)
public class MixinAbilityDescriptionFactory {

    @Redirect(method = "appendLabels", at = @At(value = "INVOKE", target = "Liskallia/vault/skill/ability/component/AbilityLabelFactory;create(Ljava/lang/String;Liskallia/vault/skill/ability/component/AbilityLabelContext;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent modifyManaLabels(String key, AbilityLabelContext<?> context) {
        if (!key.equals("manaCost")) {
            return AbilityLabelFactory.create(key, context);
        } else {
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(Minecraft.getInstance().player);
            float reduction = snapshot.getAttributeValue(ModGearAttributes.MANA_COST_REDUCTION, VaultGearAttributeTypeMerger.floatSum());
            float baseCost = Float.parseFloat(AbilityLabelBindingRegistry.getBindingValue(context.config(), key));
            int adjustedCost = Math.round(baseCost * (1 - reduction));
            return AbilityLabelFactory.create(key, context)
                    .append(" (" + adjustedCost + ")")
                    .withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("manaCost")));
        }
    }

}
