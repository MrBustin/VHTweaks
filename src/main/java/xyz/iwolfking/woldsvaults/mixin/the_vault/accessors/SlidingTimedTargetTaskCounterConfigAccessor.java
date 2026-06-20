package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.counter.SlidingTimedTargetTaskCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SlidingTimedTargetTaskCounter.Config.class, remap = false)
public interface SlidingTimedTargetTaskCounterConfigAccessor {
    @Accessor("window")
    IntRoll getWindowRoll();
}
