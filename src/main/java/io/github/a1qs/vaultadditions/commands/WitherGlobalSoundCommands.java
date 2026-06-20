package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class WitherGlobalSoundCommands {
    public WitherGlobalSoundCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("witherglobalsound")
                        .then(Commands.literal("toggle")
                                .executes(this::toggleWitherGlobalSound))
                        .then(Commands.literal("enable")
                                .executes(context -> setWitherGlobalSoundOverride(context, true)))
                        .then(Commands.literal("disable")
                                .executes(context -> setWitherGlobalSoundOverride(context, false)))
                )
        );
    }

    private int toggleWitherGlobalSound(CommandContext<CommandSourceStack> context) {
        return setWitherGlobalSoundOverride(context, !ServerConfigs.LIMIT_WITHER_SPAWN_GLOBAL_SOUND.get());
    }

    private int setWitherGlobalSoundOverride(CommandContext<CommandSourceStack> context, boolean enabled) {
        ServerConfigs.LIMIT_WITHER_SPAWN_GLOBAL_SOUND.set(enabled);
        ServerConfigs.SPEC.save();

        if (enabled) {
            context.getSource().sendSuccess(new TextComponent("Wither spawn sound is now limited to 64 blocks.").withStyle(ChatFormatting.GREEN), true);
        } else {
            context.getSource().sendSuccess(new TextComponent("Wither spawn sound is using the vanilla global sound.").withStyle(ChatFormatting.YELLOW), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int getRequiredPermissionLevel() {
        return 2;
    }
}
