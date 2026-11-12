package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.entity.ai.GoalTask;
import iskallia.vault.entity.entity.guardian.AbstractGuardianEntity;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;

public class ProtectVillagerTargetGoal extends GoalTask<Mob> {
   private final UUID villagerId;
   private LivingEntity targetEntity;

   public ProtectVillagerTargetGoal(Mob entity, UUID villagerId) {
      super(entity);
      this.villagerId = villagerId;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   public boolean canUse() {
      Level level = this.getEntity().level;
      if (!level.isClientSide && level instanceof ServerLevel world) {
         LivingEntity villager = (LivingEntity)world.getEntity(this.villagerId);
         if (villager != null && villager.isAlive()) {
            this.targetEntity = villager;
            return true;
         }
         return false;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      LivingEntity target = this.getEntity().getTarget();
      return target != null && target.isAlive() && target.getUUID().equals(this.villagerId);
   }

   public void start() {
      this.getEntity().setTarget(this.targetEntity);
      this.syncGuardianTarget();
   }

   @Override
   public void tick() {
      this.syncGuardianTarget();
   }

   private void syncGuardianTarget() {
      // Guardians inherit Piglin#getTarget(), which reads brain memory instead of Mob's target field.
      if (this.getEntity() instanceof AbstractGuardianEntity guardian && this.targetEntity != null && this.targetEntity.isAlive()) {
         guardian.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, this.targetEntity);
      }
   }

   public void stop() {
      if (this.getEntity().getTarget() != null && this.getEntity().getTarget().getUUID().equals(this.villagerId)) {
         this.getEntity().setTarget(null);
         if (this.getEntity() instanceof AbstractGuardianEntity guardian) {
            guardian.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         }
      }
      this.targetEntity = null;
   }
}
