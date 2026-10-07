package xyz.iwolfking.woldsvaults.gear.attribute.readers;

import iskallia.vault.config.ColorsConfig;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.reader.FlagModifierReader;
import iskallia.vault.init.ModConfigs;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class JavelinConductReader extends FlagModifierReader {
    public JavelinConductReader(String modifierName, int rgbColor) {
        super(modifierName, rgbColor);
    }

    @Override
    public MutableComponent getDisplay(VaultGearAttributeInstance<Boolean> instance, VaultGearModifier.AffixType type) {
        return new TextComponent("")
                .append(type.getAffixPrefixComponent(true))
                .append(new TextComponent("Scatter Javelins ").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor(ColorsConfig.UNIQUE_HIGHLIGHT))))
                .append(new TextComponent("can now trigger on hits").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xE35F12))))
                .setStyle(getColoredTextStyle());
    }
}
