package xyz.iwolfking.woldsvaults.api.gear.modification.unusual;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class CustomModifierCategoryTooltip {
    public static MutableComponent modifyUnusualTooltip(MutableComponent cmp) {
        MutableComponent abyssalCt = (new TextComponent("ᛳ ")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(29260)));
        return abyssalCt.append(cmp);
    }

}
