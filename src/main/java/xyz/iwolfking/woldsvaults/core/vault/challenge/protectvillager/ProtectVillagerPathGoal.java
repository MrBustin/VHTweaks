package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.entity.ai.GoalTask;
import iskallia.vault.entity.entity.guardian.AbstractGuardianEntity;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import xyz.iwolfking.woldsvaults.entities.ProtectVillagerEntity;

/** Follows configured Dragon Path Node waypoints before normal combat goals resume. */
public class ProtectVillagerPathGoal extends GoalTask<Mob> {
    private final UUID villagerId;
    private final List<BlockPos> pathNodes;
    private BlockPos waypoint;
    private boolean completed;

    public ProtectVillagerPathGoal(Mob mob, UUID villagerId, List<BlockPos> pathNodes) {
        super(mob);
        this.villagerId = villagerId;
        this.pathNodes = pathNodes;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.completed || this.pathNodes.isEmpty() || !this.targetsVillager()) return false;
        Mob mob = this.getEntity();
        this.waypoint = this.pathNodes.stream()
            .min(java.util.Comparator.comparingDouble(pos ->
                mob.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)))
            .orElse(null);
        return this.waypoint != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.completed && this.targetsVillager();
    }

    @Override
    public void start() {
        this.completed = false;
        if (this.getEntity() instanceof AbstractGuardianEntity guardian && guardian.level instanceof ServerLevel world) {
            // Stop existing brain movement before giving navigation to the waypoint goal.
            // The guardian mixin pauses brain ticks until this goal releases control.
            guardian.getBrain().stopAll(world, guardian);
            guardian.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            guardian.getBrain().eraseMemory(MemoryModuleType.PATH);
            guardian.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        this.navigateToWaypoint();
    }

    @Override
    public void tick() {
        if (this.waypoint == null) return;
        double dx = this.getEntity().getX() - (this.waypoint.getX() + 0.5);
        double dy = this.getEntity().getY() - (this.waypoint.getY() + 0.5);
        double dz = this.getEntity().getZ() - (this.waypoint.getZ() + 0.5);
        if (dx * dx + dy * dy + dz * dz <= 4.0) {
            this.completed = true;
        } else if (this.getEntity().getNavigation().isDone()) {
            this.navigateToWaypoint();
        }
    }

    @Override
    public void stop() {
        this.getEntity().getNavigation().stop();
    }

    private boolean targetsVillager() {
        return this.getEntity().getTarget() instanceof ProtectVillagerEntity villager
            && villager.isAlive() && villager.getUUID().equals(this.villagerId);
    }

    private void navigateToWaypoint() {
        if (this.waypoint == null) return;
        this.getEntity().getNavigation().moveTo(
            waypoint.getX() + 0.5,
            waypoint.getY() + 0.5,
            waypoint.getZ() + 0.5,
            1.0
        );
    }
}
