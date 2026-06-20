package io.github.a1qs.vaultadditions.util;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.item.render.ColorBlender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.List;

public class TextUtil {
    public static final List<String> RAINBOW = Arrays.asList(
            "#FF0000",
            "#FF7F00",
            "#FFFF00",
            "#00FF00",
            "#0000FF",
            "#4B0082",
            "#8B00FF"
    );


    public static Component blendString(String text, float interval, List<String> hexStrings) {
        ColorBlender colorBlender = new ColorBlender(1.0F);

        for(String hexString : hexStrings) {
            colorBlender.add(TextColor.parseColor(hexString).getValue(), interval);
        }

        float time = (float) ClientScheduler.INSTANCE.getTick();
        int color = colorBlender.getColor(time);
        return new TextComponent(text).setStyle(Style.EMPTY.withColor(color));
    }
}
