package xyz.iwolfking.woldsvaults.util;

import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.item.render.ColorBlender;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;

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

    public static MutableComponent getPortalComponent(int index, PortalData data) {
        return new TextComponent("=== ")
                .append(new TextComponent("Portal data of index " + index).withStyle(ChatFormatting.AQUA))
                .append(new TextComponent(" ===\n").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent("    Portal Frame Center Position: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(data.getPortalFrameCenterPos() + "\n").withStyle(ChatFormatting.YELLOW))
                .append(new TextComponent("    Portal size: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(data.getSize() + "\n").withStyle(ChatFormatting.YELLOW))
                .append(new TextComponent("    Portal Rotation: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(data.getRotation() + "\n").withStyle(ChatFormatting.YELLOW))
                .append(new TextComponent("    Portal Activation State: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(data.getActiveState() + "\n").withStyle(ChatFormatting.YELLOW))
                .append(new TextComponent("    Portal Dimension: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(data.getDimension().location().getPath() + "\n").withStyle(ChatFormatting.YELLOW));
    }

    public static MutableComponent getPortalTpComponent(int index, PortalData data) {
        BlockPos offsetPosition = data.getPortalFrameCenterPos().offset(3.0, 0.0, 3.0);
        String tpCommand = "    /execute as @s in " + data.getDimension().location() + " run tp " + offsetPosition.getX() + " " + offsetPosition.getY() + " " + offsetPosition.getZ();
        MutableComponent tpComponent = new TextComponent(tpCommand).withStyle(ChatFormatting.AQUA);
        tpComponent.withStyle((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to teleport!"))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand)));

        return TextUtil.getPortalComponent(index, data).append(tpComponent);
    }

    public static MutableComponent loginComponent() {
        return new TranslatableComponent("woldsvaults.portal.login");
    }

    public static String dimensionName(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.OVERWORLD)) {
            return "the Overworld";
        } else if (dimension.equals(Level.NETHER)) {
            return "the Nether";
        } else if (dimension.equals(Level.END)) {
            return "the End";
        } else {
            WoldsVaults.LOGGER.error("Chosen dimension for a new active portal was not the overworld, nether, or end.");
            return "Report this";
        }
    }

    public static MutableComponent dimensionComponent(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.OVERWORLD)) {
            return new TextComponent(dimensionName(dimension)).withStyle(ChatFormatting.YELLOW);
        } else if (dimension.equals(Level.NETHER)) {
            return new TextComponent(dimensionName(dimension)).withStyle(ChatFormatting.RED);
        } else if (dimension.equals(Level.END)) {
            return new TextComponent(dimensionName(dimension)).withStyle(ChatFormatting.DARK_AQUA);
        } else {
            WoldsVaults.LOGGER.error("Chosen dimension for a new active portal was not the overworld, nether, or end.");
            return new TextComponent("Report this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED);
        }
    }
}
