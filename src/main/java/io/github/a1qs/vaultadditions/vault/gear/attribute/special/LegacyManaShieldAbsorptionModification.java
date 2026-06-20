package io.github.a1qs.vaultadditions.vault.gear.attribute.special;


import com.google.gson.JsonArray;
import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatRangeModification;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class LegacyManaShieldAbsorptionModification extends FloatRangeModification {
    public static final ResourceLocation ID = VaultAdditions.id("mana_shield_absorption_cost");

    public LegacyManaShieldAbsorptionModification() {
        super(ID);
    }

    public float adjustAbsorptionDamageCost(float value, float absorptionCost) {
        return absorptionCost * (1.0F - value);
    }

    @Override
    public @Nullable MutableComponent getDisplay(SpecialAbilityGearAttribute<?, FloatValue> specialAbilityGearAttribute, Style style, VaultGearModifier.AffixType affixType) {
        MutableComponent valueDisplay = this.getValueDisplay(specialAbilityGearAttribute.getValue());
        return valueDisplay == null
                ? null
                : new TextComponent("")
                .withStyle(style)
                .append(affixType.getAffixPrefixComponent(specialAbilityGearAttribute.getValue().getValue() >= 0.0F).withStyle(specialAbilityGearAttribute.getTextStyle()))
                .append(valueDisplay.withStyle(specialAbilityGearAttribute.getHighlightStyle()))
                .append(" reduced ")
                .append(new TextComponent("Mana Shield").withStyle(Style.EMPTY.withColor(Color.CYAN.getRGB()))
                .append(" absorption mana cost"));
    }

    @Nullable
    public MutableComponent getValueDisplay(FloatValue config) {
        return new TextComponent(FORMAT.format(config.getValue() * 100.0F) + "%");
    }

    @Override
    public void serializeTextElements(JsonArray jsonArray, SpecialAbilityGearAttribute<?, FloatValue> specialAbilityGearAttribute, VaultGearModifier.AffixType affixType) {
        Component valueDisplay = this.getValueDisplay(specialAbilityGearAttribute.getValue());
        if (valueDisplay != null) {
            jsonArray.add(affixType.getAffixPrefix((specialAbilityGearAttribute.getValue().getValue() >= 0.0F)));
            jsonArray.add(valueDisplay.getString());
            jsonArray.add(" reduced Mana Shield absorption mana cost");
        }
    }


}
