package io.github.a1qs.vaultadditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.data.PowerCrystalData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CrystalCommands {
    public CrystalCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vaultadditions")
                .requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()))
                .then(Commands.literal("powerCrystals")
                        .then(Commands.literal("getContributions")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(this::getContributions)
                                )
                        )
                        .then(Commands.literal("resetContributions")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(this::resetContributions)
                                )
                        )
                        .then(Commands.literal("modifyContributionPercentage")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("percentage", FloatArgumentType.floatArg(0.0F))
                                                .executes(this::modifyContributionPercentage)
                                        )
                                )
                        )
                        .then(Commands.literal("modifyContributionAmount")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(this::modifyContributionAmount)
                                        )
                                )
                        )
                        .then(Commands.literal("contributionLeaderboard")
                                .then(Commands.argument("listPlayerAmount", IntegerArgumentType.integer(1))
                                        .executes(this::contributionLeaderboard)
                                )
                        )
                )
        );
    }

    //TODO: add commands to modify submissions of players & the whole server, % based and ints.



    private int getContributions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        PowerCrystalData data = PowerCrystalData.get(ServerLifecycleHooks.getCurrentServer());
        int contributions = data.getPlayerContributedCrystals(player.getUUID());
        double increaseAmount = ServerConfigs.GROW_PLAYER_AMOUNT.get() * contributions;
        double increaseAmountCapped = Math.min(increaseAmount, ServerConfigs.GROW_PLAYER_CAP.get());
        MutableComponent cmp = new TextComponent("=== ")
                .append(new TextComponent("Contributions for " + player.getName().getString()).withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(" ===\n")
                .append(new TextComponent("    Contributed Gemstones: ").withStyle(ChatFormatting.AQUA))
                .append(new TextComponent(contributions +"\n"))
                .append(new TextComponent("    Current additional size: ").withStyle(ChatFormatting.AQUA))
                .append(new TextComponent(increaseAmountCapped + " (Uncapped: " + increaseAmount + ")"));
        context.getSource().sendSuccess(cmp, true);

        return 0;
    }

    private int resetContributions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        PowerCrystalData data = PowerCrystalData.get(ServerLifecycleHooks.getCurrentServer());
        data.resetContributions(player);
        context.getSource().sendSuccess(new TextComponent("Reset Contributions for " + player.getName().getString()), true);
        return 0;
    }

    private int modifyContributionPercentage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        float percentage = FloatArgumentType.getFloat(context, "percentage");
        PowerCrystalData data = PowerCrystalData.get(ServerLifecycleHooks.getCurrentServer());
        data.setContributed(player, (int) (data.getPlayerContributedCrystals(player.getUUID()) * percentage));
        context.getSource().sendSuccess(new TextComponent("Modified Contributions of player '" + player.getName().getString() + "' to: " + data.getPlayerContributedCrystals(player.getUUID())), true);
        return 0;
    }

    private int modifyContributionAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PowerCrystalData data = PowerCrystalData.get(ServerLifecycleHooks.getCurrentServer());
        data.addContribution(player, amount);
        context.getSource().sendSuccess(new TextComponent("Modified Contributions of player '" + player.getName().getString() + "' to: " + data.getPlayerContributedCrystals(player.getUUID())), true);
        return 0;
    }

    private int contributionLeaderboard(CommandContext<CommandSourceStack> context) {
        MinecraftServer srv = context.getSource().getServer();
        int limit = IntegerArgumentType.getInteger(context, "listPlayerAmount");

        List<MutableComponent> topContributions = getTopContributors(PowerCrystalData.getServer().getPlayerContributionsMap(), srv, limit);
        MutableComponent cmp = new TextComponent("=== ")
                .append(new TextComponent("Contribution Leaderboard").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(new TextComponent(" ===\n"));

        int count = 1;
        for(MutableComponent contributor : topContributions) {
            cmp.append(new TextComponent("\n" + count + ". ").withStyle(ChatFormatting.YELLOW))
                    .append(contributor);
            count++;
        }

        context.getSource().sendSuccess(cmp, true);
        return 0;
    }

    public List<MutableComponent> getTopContributors(Map<UUID, Integer> playerContributions, MinecraftServer srv, int limit) {
        return playerContributions.entrySet().stream()
                // Sort entries by contribution value in descending order
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                // Map each entry to a string with player name and contribution
                .map(entry -> {
                    UUID playerUUID = entry.getKey();
                    int contribution = entry.getValue();

                    // Fetch player name from UUID
                    ServerPlayer player = srv.getPlayerList().getPlayer(playerUUID);
                    String playerName = (player != null) ? player.getName().getString() : "Unknown Player";

                    // Format as "PlayerName: Contribution"
                    return new TextComponent(playerName).withStyle(ChatFormatting.LIGHT_PURPLE)
                            .append(new TextComponent(" contributed " + contribution + " Power Crystals").withStyle(ChatFormatting.WHITE));
                })
                .collect(Collectors.toList());
    }






    private int getRequiredPermissionLevel() {
        return 2;
    }
}
