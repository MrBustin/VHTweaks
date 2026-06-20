package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.task.counter.TaskCounter;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = TaskCounter.Config.class, remap = false)
public interface TaskCounterConfigAccessor {
    @Accessor("variables")
    Map<String, Tag> getVariables();
}
