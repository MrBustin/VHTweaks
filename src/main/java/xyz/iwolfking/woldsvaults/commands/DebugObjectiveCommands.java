package xyz.iwolfking.woldsvaults.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.vault.objective.elixir.ElixirGoal;
import iskallia.vault.core.vault.objective.scavenger.ScavengerGoal;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public final class DebugObjectiveCommands {
    private static final SimpleCommandExceptionType NOT_IN_VAULT = new SimpleCommandExceptionType(new TextComponent("You must be inside an active vault."));
    private static final SimpleCommandExceptionType NO_LISTENER = new SimpleCommandExceptionType(new TextComponent("No active vault listener was found for this player."));
    private static final SimpleCommandExceptionType NOT_SCAVENGER = new SimpleCommandExceptionType(new TextComponent("Active vault objective is not a scavenger objective."));
    private static final SimpleCommandExceptionType NO_SCAVENGER_GOALS = new SimpleCommandExceptionType(new TextComponent("No active scavenger goals were found for this player."));
    private static final SimpleCommandExceptionType NOT_ELIXIR = new SimpleCommandExceptionType(new TextComponent("Active vault objective is not an elixir objective."));
    private static final SimpleCommandExceptionType NO_ELIXIR_GOAL = new SimpleCommandExceptionType(new TextComponent("No active elixir goal was found for this player."));

    private DebugObjectiveCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wv_debug_scav")
                .requires(source -> source.hasPermission(2))
                .executes(context -> completeScav(context.getSource()))
                .then(Commands.literal("complete")
                        .executes(context -> completeScav(context.getSource()))));

        dispatcher.register(Commands.literal("wv_debug_elixir")
                .requires(source -> source.hasPermission(2))
                .executes(context -> completeElixir(context.getSource()))
                .then(Commands.literal("complete")
                        .executes(context -> completeElixir(context.getSource()))));
    }

    private static int completeScav(CommandSourceStack source) throws CommandSyntaxException {
        ActiveVaultContext resolved = resolveActiveVault(source.getPlayerOrException());
        Listener listener = resolveListener(resolved.vault, resolved.player.getUUID());
        ScavengerObjective objective = resolveScavengerObjective(resolved.vault);

        objective.tickListener(resolved.world, resolved.vault, listener);

        ScavengerGoal.ObjList goals = objective.get(ScavengerObjective.GOALS).get(listener.getId());
        if (goals == null || goals.isEmpty()) {
            throw NO_SCAVENGER_GOALS.create();
        }

        int totalGoals = goals.size();
        int completedGoals = 0;
        int newlyCompletedGoals = 0;
        int itemsFilled = 0;

        for (ScavengerGoal goal : goals) {
            int current = goal.get(ScavengerGoal.CURRENT);
            int total = goal.get(ScavengerGoal.TOTAL);
            if (current < total) {
                goal.set(ScavengerGoal.CURRENT, total);
                newlyCompletedGoals++;
                itemsFilled += total - current;
            }

            if (goal.isCompleted()) {
                completedGoals++;
            }
        }

        objective.markDirty(ScavengerObjective.GOALS);
        objective.tickListener(resolved.world, resolved.vault, listener);
        objective.tickServer(resolved.world, resolved.vault);

        source.sendSuccess(new TextComponent(String.format(
                "Completed scavenger objective. Goals completed: %d/%d, newly completed: %d, items filled: %d.",
                completedGoals,
                totalGoals,
                newlyCompletedGoals,
                itemsFilled)), true);
        return newlyCompletedGoals;
    }

    private static int completeElixir(CommandSourceStack source) throws CommandSyntaxException {
        ActiveVaultContext resolved = resolveActiveVault(source.getPlayerOrException());
        Listener listener = resolveListener(resolved.vault, resolved.player.getUUID());
        ElixirObjective objective = resolveElixirObjective(resolved.vault);

        objective.tickListener(resolved.world, resolved.vault, listener);

        ElixirGoal goal = objective.get(ElixirObjective.GOALS).get(listener.getId());
        if (goal == null) {
            throw NO_ELIXIR_GOAL.create();
        }

        int current = goal.get(ElixirGoal.CURRENT);
        int target = goal.get(ElixirGoal.TARGET);
        int added = Math.max(0, target - current);

        if (added > 0) {
            goal.set(ElixirGoal.CURRENT, target);
        }

        objective.markDirty(ElixirObjective.GOALS);
        objective.tickListener(resolved.world, resolved.vault, listener);
        objective.tickServer(resolved.world, resolved.vault);

        source.sendSuccess(new TextComponent(String.format(
                "Completed elixir objective. Progress: %d/%d, added: %d.",
                goal.get(ElixirGoal.CURRENT),
                goal.get(ElixirGoal.TARGET),
                added)), true);
        return added;
    }

    private static ActiveVaultContext resolveActiveVault(ServerPlayer player) throws CommandSyntaxException {
        if (!(player.getLevel() instanceof VirtualWorld world)) {
            throw NOT_IN_VAULT.create();
        }

        Vault vault = VaultUtils.getVault(player.getLevel()).orElse(null);
        if (vault == null) {
            throw NOT_IN_VAULT.create();
        }

        return new ActiveVaultContext(player, world, vault);
    }

    private static Listener resolveListener(Vault vault, java.util.UUID playerId) throws CommandSyntaxException {
        Listener listener = vault.get(Vault.LISTENERS).get(playerId);
        if (listener == null) {
            throw NO_LISTENER.create();
        }

        return listener;
    }

    private static ScavengerObjective resolveScavengerObjective(Vault vault) throws CommandSyntaxException {
        Objectives objectives = vault.get(Vault.OBJECTIVES);
        ScavengerObjective objective = objectives == null ? null : objectives.findFirst(ScavengerObjective.class).orElse(null);
        if (objective == null) {
            throw NOT_SCAVENGER.create();
        }

        return objective;
    }

    private static ElixirObjective resolveElixirObjective(Vault vault) throws CommandSyntaxException {
        Objectives objectives = vault.get(Vault.OBJECTIVES);
        ElixirObjective objective = objectives == null ? null : objectives.findFirst(ElixirObjective.class).orElse(null);
        if (objective == null) {
            throw NOT_ELIXIR.create();
        }

        return objective;
    }

    private static final class ActiveVaultContext {
        private final ServerPlayer player;
        private final VirtualWorld world;
        private final Vault vault;

        private ActiveVaultContext(ServerPlayer player, VirtualWorld world, Vault vault) {
            this.player = player;
            this.world = world;
            this.vault = vault;
        }
    }
}
