package xyz.iwolfking.woldsvaults.gear.attribute.custom;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nullable;
import java.text.DecimalFormat;


public class PoisonTrailReader extends VaultGearModifierReader<EffectTrialAttribute> {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

    public PoisonTrailReader() {
        super("", 14901010);
    }

    @Nullable
    @Override
    public MutableComponent getDisplay(VaultGearAttributeInstance<EffectTrialAttribute> instance,
                                       VaultGearModifier.AffixType type) {
        EffectTrialAttribute value = instance.getValue();

        MutableComponent base = type.getAffixPrefixComponent(true)
                .append(new TextComponent("Leaves a trail of "))
                .append(new TextComponent("Poison")
                        .withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("uniqueHighlight"))))
                .append(new TextComponent(" for "));

        double seconds = value.getDurationTicks() / 20.0D;

        return base.append(
                        new TextComponent(FORMAT.format(seconds) + "s")
                                .withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("uniqueHighlight"))))
                .setStyle(this.getColoredTextStyle());
    }

    @Nullable
    @Override
    public MutableComponent getValueDisplay(EffectTrialAttribute value) {
        return new TextComponent(FORMAT.format(value.getDurationTicks() / 20.0D) + "s");
    }

    @Override
    protected void serializeTextElements(JsonArray out,
                                         VaultGearAttributeInstance<EffectTrialAttribute> instance,
                                         VaultGearModifier.AffixType type) {
        EffectTrialAttribute value = instance.getValue();
        out.add(type.getAffixPrefix(true));
        out.add("Leaves a trail of ");
        out.add("Poison");
        out.add(" for ");
        out.add(FORMAT.format(value.getDurationTicks() / 20.0D) + "s");
    }
}




