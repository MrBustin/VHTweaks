package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.entity.ai.GoalTask;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ProtectVillagerTargetGoal extends GoalTask<Mob> {
   private final UUID villagerId;
   private final float playerDetectionRange;
   private LivingEntity targetEntity;

   public ProtectVillagerTargetGoal(Mob entity, UUID villagerId, float playerDetectionRange) {
      super(entity);
      this.villagerId = villagerId;
      this.playerDetectionRange = playerDetectionRange;
   }

   public boolean canUse() {
      Level level = this.getEntity().level;
      if (!level.isClientSide && level instanceof ServerLevel world) {
         AABB playerDetectionBox = new AABB(
            this.getEntity().getX() - (double)this.playerDetectionRange,
            this.getEntity().getY() - (double)this.playerDetectionRange,
            this.getEntity().getZ() - (double)this.playerDetectionRange,
            this.getEntity().getX() + (double)this.playerDetectionRange,
            this.getEntity().getY() + (double)this.playerDetectionRange,
            this.getEntity().getZ() + (double)this.playerDetectionRange
         );
         if (!level.getEntitiesOfClass(Player.class, playerDetectionBox).isEmpty()) {
            return false;
         } else {
            LivingEntity villager = (LivingEntity)world.getEntity(this.villagerId);
            if (villager != null && villager.isAlive()) {
               this.targetEntity = villager;
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      LivingEntity target = this.getEntity().getTarget();
      if (target != null && target.getUUID() == this.villagerId) {
         Level level = this.getEntity().level;
         AABB playerDetectionBox = new AABB(
            this.getEntity().getX() - (double)this.playerDetectionRange,
            this.getEntity().getY() - (double)this.playerDetectionRange,
            this.getEntity().getZ() - (double)this.playerDetectionRange,
            this.getEntity().getX() + (double)this.playerDetectionRange,
            this.getEntity().getY() + (double)this.playerDetectionRange,
            this.getEntity().getZ() + (double)this.playerDetectionRange
         );
         return level.getEntitiesOfClass(Player.class, playerDetectionBox).isEmpty();
      } else {
         return false;
      }
   }

   public void start() {
      this.getEntity().setTarget(this.targetEntity);
   }

   public void stop() {
      if (this.getEntity().getTarget() != null && this.getEntity().getTarget().getUUID() == this.villagerId) {
         this.getEntity().setTarget(null);
      }
   }
}
