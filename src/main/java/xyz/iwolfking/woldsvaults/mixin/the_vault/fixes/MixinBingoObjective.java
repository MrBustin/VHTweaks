package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.BingoObjective;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(value = BingoObjective.class, remap = false)
public abstract class MixinBingoObjective extends Objective {
    @Shadow
    @Final
    public static FieldKey<TaskSource> TASK_SOURCE;

    /**
     * @author iwolfking
     * @reason Fix listener-specific bingo task contexts.
     */
    @Overwrite
    private TaskContext getContext(VirtualWorld world, Vault vault, UUID uuid) {
        this.setIfAbsent(TASK_SOURCE, () -> EntityTaskSource.ofUuids(JavaRandom.ofInternal(vault.get(Vault.SEED)), new UUID[0]));
        return TaskContext.of(EntityTaskSource.ofUuids(JavaRandom.ofInternal(vault.get(Vault.SEED)), uuid), world.getServer()).setVault(vault);
    }
}
