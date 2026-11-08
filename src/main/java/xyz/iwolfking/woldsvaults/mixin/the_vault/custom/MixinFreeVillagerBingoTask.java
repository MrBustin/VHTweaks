package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.task.BingoTask;
import iskallia.vault.task.MineBlockTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.TargetTaskCounter;
import iskallia.vault.task.renderer.BingoRenderer;
import iskallia.vault.task.renderer.TaskRenderer;
import iskallia.vault.task.source.EntityTaskSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerChallengeManager;

/** Adds an alternate completion route to Free Villager on every task-based Bingo board. */
@Mixin(value = BingoTask.class, remap = false)
public abstract class MixinFreeVillagerBingoTask {
    @Inject(method = "onAttach", at = @At("TAIL"))
    private void woldsvaults$attachProtectVillagerCompletion(TaskContext context, CallbackInfo ci) {
        BingoTask board = (BingoTask) (Object) this;
        if (context.getVault() == null) {
            return;
        }

        // Freshly generated tiles do not have a parent link back to their board.
        // Register on the board and inspect its current tiles, also covering regenerated tiles.
        // Task.onDetach releases the listener when this board is detached.
        CommonEvents.CHALLENGE_ROOM_COMPLETE.release(board);
        CommonEvents.CHALLENGE_ROOM_COMPLETE.register(board, event -> {
            if (!(event.getManager() instanceof ProtectVillagerChallengeManager)
                    || event.getVault() != context.getVault()) {
                return;
            }
            if (!(context.getSource() instanceof EntityTaskSource source)
                    || event.getPlayers().stream().noneMatch(source::matches)) {
                return;
            }
            for (int index = 0; index < board.getWidth() * board.getHeight(); index++) {
                if (board.isCompleted(index)) {
                    continue;
                }
                Task tile = board.getChild(index);
                for (MineBlockTask task : tile.getSelfAndDescendants(MineBlockTask.class)) {
                    TaskRenderer<?, ?> taskRenderer = task.getRenderer();
                    if (taskRenderer instanceof BingoRenderer.Leaf renderer
                            && "Free Villager".equals(renderer.name)
                            && task.getCounter() instanceof TargetTaskCounter<Integer, ?> counter) {
                        // Bypass the village-room condition for this alternate route. Bingo's
                        // next tick settles the tile, rewards, and completed lines normally.
                        counter.onSet(counter.getTarget(), context);
                    }
                }
            }
        });
    }
}
