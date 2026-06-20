package xyz.iwolfking.woldsvaults.api.util;

import iskallia.vault.client.util.ClientScheduler;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ComponentUtils {
    public static MutableComponent wavingComponent(MutableComponent base, TextColor baseColor, float frequency, float amplitude) {
        return wavingComponent(base, baseColor.getValue(), frequency, amplitude);
    }

    public static MutableComponent wavingComponent(MutableComponent base, int color, float frequency, float amplitude) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            return base;
        }

        String text = base.getString();
        MutableComponent result = new TextComponent("");
        float time = ClientScheduler.INSTANCE.getTick();

        for (int i = 0; i < text.length(); i++) {
            float wave = (float)Math.sin((time - i) * frequency) * amplitude + 1.0F;
            result.append(new TextComponent(String.valueOf(text.charAt(i)))
                    .withStyle(base.getStyle().withColor(ColorUtil.brightenColor(color, wave))));
        }

        return result;
    }
}
