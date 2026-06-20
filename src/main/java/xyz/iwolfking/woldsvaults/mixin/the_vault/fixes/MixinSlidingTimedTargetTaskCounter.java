package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.counter.SlidingTimedTargetTaskCounter;
import iskallia.vault.task.counter.TargetTaskCounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.WoldVaultUtils;
import xyz.iwolfking.woldsvaults.mixin.the_vault.accessors.SlidingTimedTargetTaskCounterConfigAccessor;
import xyz.iwolfking.woldsvaults.mixin.the_vault.accessors.TaskCounterConfigAccessor;
import xyz.iwolfking.woldsvaults.objectives.BallisticBingoObjective;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = SlidingTimedTargetTaskCounter.class, remap = false)
public abstract class MixinSlidingTimedTargetTaskCounter {
    @Shadow
    private int window;

    @Inject(method = "onPopulate", at = @At("TAIL"))
    private void repairBallisticBingoTimedCounterWindow(TaskContext context, CallbackInfo ci) {
        if (this.window > 0 || !isBallisticBingo(context)) {
            return;
        }

        Object self = this;
        if (!(self instanceof SlidingTimedTargetTaskCounter<?, ?> counter)) {
            return;
        }

        int repairedWindow = getConfiguredWindow(counter, context);
        if (repairedWindow <= 0) {
            repairedWindow = getFallbackWindow(counter);
        }

        if (repairedWindow > 0) {
            this.window = repairedWindow;
        }
    }

    @Inject(method = "onAdd", at = @At("TAIL"))
    private void markBallisticBingoTimedCounterDirty(Object value, TaskContext context, CallbackInfo ci) {
        markBallisticTaskDirty(context);
    }

    @Inject(method = "onRemove", at = @At("TAIL"))
    private void markBallisticBingoTimedCounterDirtyOnRemove(Object value, TaskContext context, CallbackInfo ci) {
        markBallisticTaskDirty(context);
    }

    @Unique
    private static boolean isBallisticBingo(TaskContext context) {
        return context.getVault() != null
                && WoldVaultUtils.getObjective(context.getVault(), BallisticBingoObjective.class) instanceof BallisticBingoObjective;
    }

    @Unique
    private static int getConfiguredWindow(SlidingTimedTargetTaskCounter<?, ?> counter, TaskContext context) {
        try {
            IntRoll windowRoll = ((SlidingTimedTargetTaskCounterConfigAccessor) counter.getConfig()).getWindowRoll();
            return windowRoll == null ? 0 : windowRoll.get(context.getSource().getRandom());
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    @Unique
    private static int getFallbackWindow(SlidingTimedTargetTaskCounter<?, ?> counter) {
        float contribution = getTargetPlayerContribution(counter);
        if (contribution == 0.0F) {
            return 5;
        }

        if (contribution == 0.25F) {
            return 600;
        }

        return 120;
    }

    @Unique
    private static float getTargetPlayerContribution(SlidingTimedTargetTaskCounter<?, ?> counter) {
        try {
            Map<String, Tag> variables = ((TaskCounterConfigAccessor) counter.getConfig()).getVariables();
            Tag tag = variables.get("targetPlayerContribution");
            if (tag instanceof NumericTag numericTag) {
                return numericTag.getAsFloat();
            }
        } catch (RuntimeException ignored) {
        }

        return 0.75F;
    }

    private static void markBallisticTaskDirty(TaskContext context) {
        if (context.getVault() == null) {
            return;
        }

        if (!(WoldVaultUtils.getObjective(context.getVault(), BallisticBingoObjective.class) instanceof BallisticBingoObjective bingo)) {
            return;
        }

        if (bingo.has(BallisticBingoObjective.TASK)) {
            bingo.markDirty(BallisticBingoObjective.TASK);
        }

        if (bingo.has(BallisticBingoObjective.TASKS)) {
            BingoObjective.TaskMap tasks = bingo.get(BallisticBingoObjective.TASKS);
            new ArrayList<>(tasks.entrySet()).forEach(entry -> tasks.put(entry.getKey(), entry.getValue()));
        }
    }
}
