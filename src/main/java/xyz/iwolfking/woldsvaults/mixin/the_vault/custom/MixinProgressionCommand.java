package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import iskallia.vault.command.progression.ProgressionCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.configs.PlayerCapesConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.world.data.DiscoveredCapesData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(value = ProgressionCommand.class, remap = false)
public abstract class MixinProgressionCommand {

    @Inject(method = "build", at = @At("TAIL"))
    private void woldsvaults$addCapeCommands(LiteralArgumentBuilder<CommandSourceStack> builder, CallbackInfo ci) {
        builder.then(Commands.literal("discover")
                .then(Commands.literal("capes")
                        .executes(MixinProgressionCommand::woldsvaults$discoverAllCapes))
                .then(Commands.literal("cape")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(MixinProgressionCommand::woldsvaults$suggestCapes)
                                        .executes(MixinProgressionCommand::woldsvaults$discoverCape)))));

        builder.then(Commands.literal("undiscover")
                .then(Commands.literal("cape")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(MixinProgressionCommand::woldsvaults$suggestCapes)
                                        .executes(MixinProgressionCommand::woldsvaults$revokeCape)))));
    }

    private static int woldsvaults$discoverAllCapes(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        DiscoveredCapesData data = DiscoveredCapesData.get(player.getLevel());
        Map<String, PlayerCapesConfig.Cape> capes = ModConfigs.PLAYER_CAPES == null
                ? Map.of()
                : ModConfigs.PLAYER_CAPES.getAll();
        for (String id : capes.keySet()) {
            data.discoverCape(player.getUUID(), id);
        }
        context.getSource().sendSuccess(new TextComponent("Discovered all capes"), false);
        return 1;
    }

    private static int woldsvaults$discoverCape(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        String capeId = StringArgumentType.getString(context, "id");
        if (ModConfigs.PLAYER_CAPES == null || ModConfigs.PLAYER_CAPES.get(capeId) == null) {
            context.getSource().sendFailure(new TextComponent("Unknown cape id: " + capeId));
            return 0;
        }

        DiscoveredCapesData.get(target.getLevel()).discoverCapeAndBroadcast(target, capeId);
        context.getSource().sendSuccess(new TextComponent(
                "Granted cape '" + capeId + "' to " + target.getName().getString()), true);
        return 1;
    }

    private static int woldsvaults$revokeCape(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        String capeId = StringArgumentType.getString(context, "id");
        if (ModConfigs.PLAYER_CAPES == null || ModConfigs.PLAYER_CAPES.get(capeId) == null) {
            context.getSource().sendFailure(new TextComponent("Unknown cape id: " + capeId));
            return 0;
        }

        boolean revoked = DiscoveredCapesData.get(target.getLevel()).revokeCape(target.getUUID(), capeId);
        if (!revoked) {
            context.getSource().sendFailure(new TextComponent(
                    target.getName().getString() + " has not discovered cape '" + capeId + "'"));
            return 0;
        }
        context.getSource().sendSuccess(new TextComponent(
                "Revoked cape '" + capeId + "' from " + target.getName().getString()), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> woldsvaults$suggestCapes(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        if (ModConfigs.PLAYER_CAPES == null) {
            return builder.buildFuture();
        }
        return SharedSuggestionProvider.suggest(
                ModConfigs.PLAYER_CAPES.getAll().keySet().stream().sorted(), builder);
    }
}
