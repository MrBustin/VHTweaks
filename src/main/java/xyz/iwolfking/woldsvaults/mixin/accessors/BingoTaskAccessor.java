package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.task.BingoTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BingoTask.class, remap = false)
public interface BingoTaskAccessor {
    @Accessor("settledTasks")
    boolean[] getSettledTasks();

    @Accessor("settledBingos")
    boolean[] getSettledBingos();

    @Invoker("onComplete")
    void callOnComplete(Task task, TaskContext context);

    @Invoker("onBingo")
    void callOnBingo(TaskContext context);
}
