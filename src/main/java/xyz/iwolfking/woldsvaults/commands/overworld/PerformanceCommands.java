package xyz.iwolfking.woldsvaults.commands.overworld;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import xyz.iwolfking.woldsvaults.world.overworld.OverworldVaultsPerformanceSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class PerformanceCommands extends BaseCommand {
    @Override
    public String getName() {
        return "performance";
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("crystal").executes(context -> setMode(context, OverworldVaultsPerformanceSavedData.Mode.CRYSTAL)));
        builder.then(Commands.literal("portal").executes(context -> setMode(context, OverworldVaultsPerformanceSavedData.Mode.PORTAL)));
    }

    private int setMode(CommandContext<CommandSourceStack> context, OverworldVaultsPerformanceSavedData.Mode mode) {
        OverworldVaultsPerformanceSavedData.get(context.getSource().getServer()).setMode(mode);
        context.getSource().sendSuccess(
                new TextComponent("OverworldVaults performance mode set to " + mode.name().toLowerCase() + ".").withStyle(ChatFormatting.YELLOW),
                true
        );
        return 0;
    }
}
