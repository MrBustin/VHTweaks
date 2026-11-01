package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity.State;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerAnimation;
import iskallia.vault.core.vault.challenge.curse.renderer.context.ChallengeRenderContext;
import iskallia.vault.core.world.storage.BlockCuboid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ProtectVillagerChallengeManager extends ChallengeManager {
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/challenge/protect_villager.png");

   static {
      Adapters.CHALLENGE_MANAGER.register(
         "woldsvaults:protect-villager",
         ProtectVillagerChallengeManager.class,
         ProtectVillagerChallengeManager::new
      );
   }

   private BlockCuboid zone;
   private ProtectVillagerChallengeManager.Phase phase;
   private final ProtectVillagerSpawner spawner = new ProtectVillagerSpawner();
   private final ProtectVillagerAnimation animation = new ProtectVillagerAnimation();
   private final List<ChallengeAction<?>> successActions = new ArrayList<>();
   private final List<ChallengeAction<?>> failureActions = new ArrayList<>();
   private final List<ChallengeAction<?>> scheduledSuccessActions = new ArrayList<>();
   private final List<ChallengeAction<?>> scheduledFailureActions = new ArrayList<>();
   private UUID villagerId;
   private double villagerHealth;
   private double villagerMaxHealth;

   public ProtectVillagerChallengeManager() {
      this.phase = ProtectVillagerChallengeManager.Phase.IDLE;
   }

   public ProtectVillagerChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      super(uuid, dimension, pos);
      this.phase = ProtectVillagerChallengeManager.Phase.IDLE;
   }

   public void setMobTypes(List<EntityType<?>> types) {
      this.spawner.setMobTypes(types);
   }

   public void setMaxWaves(int waves) {
      this.spawner.setMaxWaves(waves);
   }

   public void addSuccessActions(List<ChallengeAction<?>> actions) {
      this.scheduledSuccessActions.addAll(actions);
   }

   public void addFailureActions(List<ChallengeAction<?>> actions) {
      this.scheduledFailureActions.addAll(actions);
   }

   @Override
   public void onAttach(ServerLevel world) {
      super.onAttach(world);
      this.spawner.onAttach(world);
      this.registerZone(world);
      Villager villager = (Villager)EntityType.VILLAGER.create(world);
      if (villager != null) {
         villager.moveTo((double)this.pos.getX() + 0.5, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 0.5, 0.0F, 0.0F);
         world.addFreshEntity(villager);
         this.villagerId = villager.getUUID();
         this.villagerMaxHealth = (double)villager.getMaxHealth();
         this.villagerHealth = (double)villager.getHealth();
         this.spawner.setTargetVillager(villager.getUUID());
      } else {
         VaultMod.LOGGER.warn("Failed to create villager for ProtectVillager challenge at {}", this.pos);
         this.phase = ProtectVillagerChallengeManager.Phase.FAILED;
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      this.spawner.onDetach();
   }

   @Override
   public boolean addPlayer(Player player) {
      if (!super.addPlayer(player)) {
         return false;
      } else {
         for (ChallengeAction<?> action : this.successActions) {
            action.onAddPlayer(player);
         }

         for (ChallengeAction<?> action : this.failureActions) {
            action.onAddPlayer(player);
         }

         return true;
      }
   }

   @Override
   public boolean removePlayer(Player player) {
      if (!super.removePlayer(player)) {
         return false;
      } else {
         for (ChallengeAction<?> action : this.successActions) {
            action.onRemovePlayer(player);
         }

         for (ChallengeAction<?> action : this.failureActions) {
            action.onRemovePlayer(player);
         }

         return true;
      }
   }

   private void computeScheduledActions(ServerLevel world) {
      this.computeActions(this.scheduledSuccessActions, this.successActions, world);
      this.computeActions(this.scheduledFailureActions, this.failureActions, world);
   }

   @Override
   public void onTick(ServerLevel world) {
      if (!this.isDeleted()) {
         this.refreshPlayers(world);
         if (!this.scheduledSuccessActions.isEmpty() || !this.scheduledFailureActions.isEmpty()) {
            this.computeScheduledActions(world);
         }

         this.animation.onTick(world, this.pos);
         super.onTick(world);
         if (this.getBlockEntity(world.getServer()) instanceof ChallengeControllerBlockEntity<?> controller
            && controller.getState() == State.ACTIVE
            && this.phase == ProtectVillagerChallengeManager.Phase.IDLE
            && !this.players.isEmpty()) {
            this.phase = ProtectVillagerChallengeManager.Phase.STARTING;
            this.animation.onStart(xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerAnimation.State.CLOSE_ROOM);
         }

         if (this.phase == ProtectVillagerChallengeManager.Phase.STARTING
            && this.animation.getState() == xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerAnimation.State.IDLE) {
            this.phase = ProtectVillagerChallengeManager.Phase.ACTIVE;
            this.spawner.spawnWave(world, this.pos);
            VaultMod.LOGGER.info("Spawning ProtectVillager challenge at {}", this.pos);
         }

         if (this.phase == ProtectVillagerChallengeManager.Phase.ACTIVE) {
            if (world.getEntity(this.villagerId) instanceof Villager villager) {
               this.villagerHealth = (double)villager.getHealth();
               this.villagerMaxHealth = (double)villager.getMaxHealth();
               if (!villager.isAlive()) {
                  this.fail(world);
               }
            } else {
               this.fail(world);
            }

            this.spawner.onTick(world);
            if (this.spawner.isWaveCleared()) {
               if (this.spawner.getWave() >= this.spawner.getMaxWaves()) {
                  this.success(world);
                  VaultMod.LOGGER.info("Success!");
               } else {
                  this.spawner.spawnWave(world, this.pos);
                  VaultMod.LOGGER.info("Spawned new wave");
               }
            }
         }
      }
   }

   private void fail(ServerLevel world) {
      if (this.phase != ProtectVillagerChallengeManager.Phase.FAILED && this.phase != ProtectVillagerChallengeManager.Phase.COMPLETED) {
         this.phase = ProtectVillagerChallengeManager.Phase.FAILED;
         this.invokeFailEvent(world);
         this.animation.onStart(xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerAnimation.State.OPEN_ROOM);
         RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

         for (ChallengeAction<?> action : this.failureActions) {
            action.onActivate(world, this, random);
         }
      }
   }

   private void success(ServerLevel world) {
      if (this.phase != ProtectVillagerChallengeManager.Phase.FAILED && this.phase != ProtectVillagerChallengeManager.Phase.COMPLETED) {
         this.phase = ProtectVillagerChallengeManager.Phase.COMPLETED;
         Entity entity = world.getEntity(this.villagerId);
         if (entity != null) {
            entity.discard();
         }

         this.animation.onStart(xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerAnimation.State.OPEN_ROOM);
         RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

         for (ChallengeAction<?> action : this.successActions) {
            action.onActivate(world, this, random);
         }

         world.playSound(null, this.pos, SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, 0.8F, 1.0F);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
         Adapters.UUID.writeNbt(this.villagerId).ifPresent(tag -> nbt.put("villager", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.villagerHealth)).ifPresent(tag -> nbt.put("villagerHealth", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.villagerMaxHealth)).ifPresent(tag -> nbt.put("villagerMaxHealth", tag));
         this.writeActionsNbt(nbt, "successActions", this.successActions);
         this.writeActionsNbt(nbt, "failureActions", this.failureActions);
         this.writeActionsNbt(nbt, "scheduledSuccessActions", this.scheduledSuccessActions);
         this.writeActionsNbt(nbt, "scheduledFailureActions", this.scheduledFailureActions);
         this.spawner.writeNbt().ifPresent(tag -> nbt.put("spawner", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.villagerId = Adapters.UUID.readNbt(nbt.get("villager")).orElse(null);
      this.villagerHealth = Adapters.DOUBLE.readNbt(nbt.get("villagerHealth")).orElse(0.0);
      this.villagerMaxHealth = Adapters.DOUBLE.readNbt(nbt.get("villagerMaxHealth")).orElse(0.0);
      this.readActionsNbt(nbt, "successActions", this.successActions);
      this.readActionsNbt(nbt, "failureActions", this.failureActions);
      this.readActionsNbt(nbt, "scheduledSuccessActions", this.scheduledSuccessActions);
      this.readActionsNbt(nbt, "scheduledFailureActions", this.scheduledFailureActions);
      if (nbt.contains("spawner")) {
         this.spawner.readNbt(nbt.getCompound("spawner"));
      }
   }

   @Override
   public boolean shouldRenderObjectives() {
      return this.phase != ProtectVillagerChallengeManager.Phase.ACTIVE;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onRender(ChallengeRenderContext context) {
      if (this.phase == ProtectVillagerChallengeManager.Phase.ACTIVE) {
         double progress = this.villagerMaxHealth <= 0.0 ? 0.0 : this.villagerHealth / this.villagerMaxHealth;
         this.renderProgressBar(context, HUD, progress, null);
      }
   }

   @Generated
   @Override
   public BlockCuboid getZone() {
      return this.zone;
   }

   @Generated
   @Override
   public void setZone(BlockCuboid zone) {
      this.zone = zone;
   }

   @Generated
   public ProtectVillagerChallengeManager.Phase getPhase() {
      return this.phase;
   }

   @Generated
   public void setPhase(ProtectVillagerChallengeManager.Phase phase) {
      this.phase = phase;
   }

   public static enum Phase {
      IDLE,
      STARTING,
      ACTIVE,
      FAILED,
      COMPLETED;
   }
}
