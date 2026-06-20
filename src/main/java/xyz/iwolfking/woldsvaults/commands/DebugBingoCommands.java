package xyz.iwolfking.woldsvaults.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.mixin.accessors.BingoObjectiveAccessor;
import xyz.iwolfking.woldsvaults.mixin.accessors.BingoTaskAccessor;
import xyz.iwolfking.woldsvaults.objectives.BallisticBingoObjective;

import java.util.Arrays;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public final class DebugBingoCommands {
    private static final SimpleCommandExceptionType NOT_IN_VAULT = new SimpleCommandExceptionType(new TextComponent("You must be inside an active vault."));
    private static final SimpleCommandExceptionType NOT_BINGO = new SimpleCommandExceptionType(new TextComponent("Active vault objective is not a bingo objective."));
    private static final SimpleCommandExceptionType NO_BINGO_BOARD = new SimpleCommandExceptionType(new TextComponent("No active bingo board was found for this player."));
    private static final SimpleCommandExceptionType NO_SELECTED_LINE = new SimpleCommandExceptionType(new TextComponent("Could not resolve the currently selected bingo line."));

    private DebugBingoCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wv_debug_bingo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("complete_current_row")
                        .executes(context -> completeCurrentRow(context.getSource())))
                .then(Commands.literal("blackout")
                        .executes(context -> blackout(context.getSource()))));
    }

    private static int completeCurrentRow(CommandSourceStack source) throws CommandSyntaxException {
        ResolvedBoard resolved = resolveBoard(source.getPlayerOrException());
        int lineIndex = getSelectedLineIndex(resolved.board, resolved.playerId);
        if (lineIndex < 0) {
            throw NO_SELECTED_LINE.create();
        }

        int completedTasks = completeLine(resolved, resolved.board.getLine(lineIndex));
        int completedBingos = settleNewBingos(resolved);
        syncObjectiveTask(resolved);

        source.sendSuccess(new TextComponent(String.format("Completed selected bingo line %d. Cells completed: %d, new bingos: %d, total bingos: %d.",
                lineIndex,
                completedTasks,
                completedBingos,
                resolved.board.getCompletedBingos())), true);
        return completedBingos;
    }

    private static int blackout(CommandSourceStack source) throws CommandSyntaxException {
        ResolvedBoard resolved = resolveBoard(source.getPlayerOrException());
        int[] allIndices = new int[resolved.board.getWidth() * resolved.board.getHeight()];
        for (int index = 0; index < allIndices.length; index++) {
            allIndices[index] = index;
        }

        int completedTasks = completeLine(resolved, allIndices);
        int completedBingos = settleNewBingos(resolved);
        syncObjectiveTask(resolved);

        source.sendSuccess(new TextComponent(String.format("Completed blackout. Cells completed: %d, new bingos: %d, total bingos: %d.",
                completedTasks,
                completedBingos,
                resolved.board.getCompletedBingos())), true);
        return completedBingos;
    }

    private static ResolvedBoard resolveBoard(ServerPlayer player) throws CommandSyntaxException {
        if (!(player.getLevel() instanceof VirtualWorld world)) {
            throw NOT_IN_VAULT.create();
        }

        Vault vault = VaultUtils.getVault(player.getLevel()).orElse(null);
        if (vault == null) {
            throw NOT_IN_VAULT.create();
        }

        Objectives objectives = vault.get(Vault.OBJECTIVES);
        BingoObjective objective = objectives == null ? null : objectives.findFirst(BingoObjective.class).orElse(null);
        if (objective == null) {
            throw NOT_BINGO.create();
        }

        boolean pvp = VaultUtils.isPvPVault(vault);
        BingoTask board;
        TaskContext context;
        if (pvp) {
            board = (BingoTask)getTaskMap(objective).get(player.getUUID());
            context = ((BingoObjectiveAccessor)objective).getContext(world, vault, player.getUUID());
        } else {
            board = (BingoTask)getRootTask(objective);
            context = objective.getContext(world, vault);
        }

        if (board == null) {
            throw NO_BINGO_BOARD.create();
        }

        return new ResolvedBoard(player.getUUID(), world, vault, objective, board, context, pvp);
    }

    private static int getSelectedLineIndex(BingoTask board, UUID playerId) {
        int[] selected = board.getSelectedLine(playerId);
        for (int lineIndex = 0; lineIndex < board.getMaxBingos(); lineIndex++) {
            if (Arrays.equals(selected, board.getLine(lineIndex))) {
                return lineIndex;
            }
        }

        return -1;
    }

    private static int completeLine(ResolvedBoard resolved, int[] indices) {
        BingoTaskAccessor accessor = (BingoTaskAccessor)resolved.board;
        boolean[] settledTasks = accessor.getSettledTasks();
        int completedTasks = 0;

        for (int index : indices) {
            if (settledTasks[index]) {
                continue;
            }

            Task task = resolved.board.getChild(index);
            accessor.callOnComplete(task, resolved.context);
            settledTasks[index] = true;
            task.onDetach();
            completedTasks++;
        }

        return completedTasks;
    }

    private static int settleNewBingos(ResolvedBoard resolved) {
        BingoTaskAccessor accessor = (BingoTaskAccessor)resolved.board;
        boolean[] settledBingos = accessor.getSettledBingos();
        int newBingos = 0;

        for (int lineIndex = 0; lineIndex < resolved.board.getMaxBingos(); lineIndex++) {
            if (!settledBingos[lineIndex] && resolved.board.isBingo(lineIndex)) {
                accessor.callOnBingo(resolved.context);
                settledBingos[lineIndex] = true;
                newBingos++;
            }
        }

        return newBingos;
    }

    private static void syncObjectiveTask(ResolvedBoard resolved) {
        if (resolved.pvp) {
            getTaskMap(resolved.objective).put(resolved.playerId, resolved.board);
        } else {
            resolved.objective.markDirty(resolved.objective instanceof BallisticBingoObjective ? BallisticBingoObjective.TASK : BingoObjective.TASK);
        }
    }

    private static Task getRootTask(BingoObjective objective) {
        return objective instanceof BallisticBingoObjective
                ? objective.get(BallisticBingoObjective.TASK)
                : objective.get(BingoObjective.TASK);
    }

    private static BingoObjective.TaskMap getTaskMap(BingoObjective objective) {
        return objective instanceof BallisticBingoObjective
                ? objective.get(BallisticBingoObjective.TASKS)
                : objective.get(BingoObjective.TASKS);
    }

    private static final class ResolvedBoard {
        private final UUID playerId;
        private final VirtualWorld world;
        private final Vault vault;
        private final BingoObjective objective;
        private final BingoTask board;
        private final TaskContext context;
        private final boolean pvp;

        private ResolvedBoard(UUID playerId, VirtualWorld world, Vault vault, BingoObjective objective, BingoTask board, TaskContext context, boolean pvp) {
            this.playerId = playerId;
            this.world = world;
            this.vault = vault;
            this.objective = objective;
            this.board = board;
            this.context = context;
            this.pvp = pvp;
        }
    }
}
