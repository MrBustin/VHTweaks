package xyz.iwolfking.woldsvaults.modifiers.gear.special;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.IntRangeModification;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class GlacialBlastModification extends IntRangeModification {
    public static final ResourceLocation ID = VaultMod.id("glacial_blast_hypothermia");

    public GlacialBlastModification() {
        super(ID);
    }

    @Nullable
    public MutableComponent getDisplay(SpecialAbilityGearAttribute<?, IntValue> instance, Style style, VaultGearModifier.AffixType type) {
        MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
        return valueDisplay == null ? null : (new TextComponent("")).append(type.getAffixPrefixComponent(true)).append("Glacial Blast is ").append(valueDisplay.withStyle(instance.getHighlightStyle())).setStyle(instance.getTextStyle()).append("X more likely to Glacially Imprison");
    }


    public void serializeTextElements(JsonArray out, SpecialAbilityGearAttribute<?, IntValue> instance, VaultGearModifier.AffixType type) {
        MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
        if (valueDisplay != null) {
            out.add(type.getAffixPrefix(true));
            out.add("Glacial Blast is ");
            out.add(valueDisplay.getString());
            out.add("X more likely to Glacially Imprison");
        }
    }
}
