package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity.State;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.core.vault.challenge.action.MultiplyActionsChallengeAction;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.raid.RaidActionEntry;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import xyz.iwolfking.woldsvaults.init.ModEntities;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerProxyBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static iskallia.vault.core.vault.Vault.SEED;

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
   private Phase phase;
   private final ProtectVillagerSpawner spawner = new ProtectVillagerSpawner();
   private final List<ChallengeAction<?>> successActions = new ArrayList<>();
   private final List<ChallengeAction<?>> failureActions = new ArrayList<>();
   private final List<ChallengeAction<?>> scheduledSuccessActions = new ArrayList<>();
   private final List<ChallengeAction<?>> scheduledFailureActions = new ArrayList<>();
   private UUID villagerId;
   private double villagerHealth;
   private double villagerMaxHealth;
   private final List<ChallengeAction<?>> actions = new ArrayList<>();
   private final List<ChallengeAction<?>> scheduledActions = new ArrayList<>();
   private final java.util.Map<BlockPos, List<ChallengeAction<?>>> proxies = new java.util.LinkedHashMap<>();
   private int proxyTicker;

   public ProtectVillagerChallengeManager() {
      this.phase = Phase.IDLE;
   }

   public ProtectVillagerChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos) {
      super(uuid, dimension, pos);
      this.phase = Phase.IDLE;
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
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY)
         .register(this, event -> {
            if (event.getPlayer() == null || !(world.getBlockEntity(event.getPos()) instanceof ProtectVillagerControllerProxyBlockEntity proxy)) return;
            this.onProxyClick(world, event.getPlayer(), proxy);
         });
      this.registerZone(world);
      Villager villager = ModEntities.PROTECT_VILLAGER.create(world);
      if (villager != null) {
         villager.setCustomName(new TextComponent("Dinnerbone"));
         villager.moveTo((double)this.pos.getX() + 0.5, (double)(this.pos.getY() + 8), (double)this.pos.getZ() + 0.5, 0.0F, 0.0F);
         world.addFreshEntity(villager);
         this.villagerId = villager.getUUID();
         this.villagerMaxHealth = (double)villager.getMaxHealth();
         this.villagerHealth = (double)villager.getHealth();
         this.spawner.setTargetVillager(villager.getUUID());
      } else {
         VaultMod.LOGGER.warn("Failed to create villager for ProtectVillager challenge at {}", this.pos);
         this.phase = Phase.FAILED;
      }
   }

   @Override
   public void onDetach() {
      super.onDetach();
      this.spawner.onDetach();
      CommonEvents.BLOCK_USE.release(this);
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

         super.onTick(world);
         if (this.getBlockEntity(world.getServer()) instanceof ChallengeControllerBlockEntity<?> controller
            && controller.getState() == State.ACTIVE
            && this.phase == Phase.IDLE
            && !this.players.isEmpty()) {
            this.phase = Phase.PROXIES;
            this.spawnRewardProxies(world);
         }

         if (this.phase == Phase.IN_WAVE) {
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
                  this.phase = Phase.PROXIES;
                  this.spawnContinueProxies(world);
               }
            }
         }
      }
   }

   private void spawnRewardProxies(ServerLevel world) {
      this.spawnProxies(world, List.of("Gilded", "Ornate", "Living", "Coin Piles"));
   }

   private void spawnContinueProxies(ServerLevel world) {
      this.spawnProxies(world, List.of("Double It", "Forfeit and Loot"));
   }

   private void spawnProxies(ServerLevel world, List<String> labels) {
      this.clearProxies(world);
      this.proxies.clear();
      int[][] offsets = labels.size() == 4
         ? new int[][]{{-2, 0, -2}, {2, 0, -2}, {-2, 0, 2}, {2, 0, 2}}
         : new int[][]{{-2, 0, 0}, {2, 0, 0}};
      for (int i = 0; i < labels.size(); i++) {
         BlockPos offset = new BlockPos(offsets[i][0], offsets[i][1], offsets[i][2]);
         BlockPos proxyPos = this.pos.offset(offset);
         List<ChallengeAction<?>> optionActions = new ArrayList<>();
         if (labels.size() == 2 && i == 0) {
            optionActions.addAll(this.actions);
            optionActions.addAll(this.actions);
         }
         this.proxies.put(offset, optionActions);
         IZonedWorld.runWithBypass(world, true, () -> world.setBlock(proxyPos, ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY.defaultBlockState(), 3));
         if (world.getBlockEntity(proxyPos) instanceof ProtectVillagerControllerProxyBlockEntity proxy) {
            proxy.setController(this.pos);
            proxy.setActions(optionActions);
            proxy.setLabel(labels.get(i));
         }
      }
      this.proxyTicker = 0;
   }

   private void clearProxies(ServerLevel world) {
      IZonedWorld.runWithBypass(world, true, () -> {
         for (BlockPos offset : this.proxies.keySet()) {
            BlockPos proxyPos = this.pos.offset(offset);
            world.destroyBlock(proxyPos, false);
            forceBlockSync(world, proxyPos);
         }
      });
   }

   public void onProxyClick(ServerLevel world, Player player, ProtectVillagerControllerProxyBlockEntity proxy) {
      if (this.phase != Phase.PROXIES) return;
      String label = proxy.getLabel();
      this.clearProxies(world);
      this.proxies.clear();
      if ("Forfeit and Loot".equals(label)) {
         this.phase = Phase.FORFEITED;
         this.success(world);
         return;
      }
      for (ChallengeAction<?> action : proxy.getActions()) {
         this.scheduledActions.add(action);
      }
      if ("Double It".equals(label)) this.actions.addAll(new ArrayList<>(this.actions));
      if ("Gilded".equals(label) || "Ornate".equals(label) || "Living".equals(label) || "Coin Piles".equals(label)) {
         this.actions.addAll(this.scheduledActions);
         this.scheduledActions.clear();
      }
      this.phase = Phase.IN_WAVE;
      this.spawner.spawnWave(world, this.pos);
      ServerScheduler.INSTANCE.schedule(20, () -> world.playSound(null, this.pos, ModSounds.ARENA_HORNS_SFX, SoundSource.BLOCKS, 1.0F, 1.0F));
   }

   private void fail(ServerLevel world) {
      if (this.phase != Phase.FAILED && this.phase != Phase.COMPLETED) {
         this.phase = Phase.FAILED;
         this.invokeFailEvent(world);
         RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

         for (ChallengeAction<?> action : this.failureActions) {
            action.onActivate(world, this, random);
         }
      }
   }

   private void success(ServerLevel world) {
      if (this.phase != Phase.FAILED && this.phase != Phase.COMPLETED) {
         this.phase = Phase.COMPLETED;
         Entity entity = world.getEntity(this.villagerId);
         if (entity != null) {
            entity.discard();
         }

         RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

         for (ChallengeAction<?> action : this.successActions) {
            action.onActivate(world, this, random);
         }

         for (ChallengeAction<?> action : this.actions) {
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
         nbt.putString("phase", this.phase.name());
         this.writeActionsNbt(nbt, "successActions", this.successActions);
         this.writeActionsNbt(nbt, "failureActions", this.failureActions);
         this.writeActionsNbt(nbt, "scheduledSuccessActions", this.scheduledSuccessActions);
         this.writeActionsNbt(nbt, "scheduledFailureActions", this.scheduledFailureActions);
         this.writeActionsNbt(nbt, "rewardActions", this.actions);
         this.writeActionsNbt(nbt, "scheduledRewardActions", this.scheduledActions);
         nbt.putInt("proxyTicker", this.proxyTicker);
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
      if (nbt.contains("phase")) {
         try {
            this.phase = Phase.valueOf(nbt.getString("phase"));
         } catch (IllegalArgumentException ignored) {
            this.phase = Phase.IDLE;
         }
      }
      this.readActionsNbt(nbt, "successActions", this.successActions);
      this.readActionsNbt(nbt, "failureActions", this.failureActions);
      this.readActionsNbt(nbt, "scheduledSuccessActions", this.scheduledSuccessActions);
      this.readActionsNbt(nbt, "scheduledFailureActions", this.scheduledFailureActions);
      this.readActionsNbt(nbt, "rewardActions", this.actions);
      this.readActionsNbt(nbt, "scheduledRewardActions", this.scheduledActions);
      this.proxyTicker = nbt.getInt("proxyTicker");
      if (nbt.contains("spawner")) {
         this.spawner.readNbt(nbt.getCompound("spawner"));
      }
   }

   @Override
   public boolean shouldRenderObjectives() {
      return this.phase != Phase.IN_WAVE;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onRender(ChallengeRenderContext context) {

      int currentWave = this.spawner.getWave();
      int maxWaves = this.spawner.getMaxWaves();
      Component txt;

      if (maxWaves < 0 || currentWave < maxWaves) {
         txt = new TextComponent("").withStyle(ChatFormatting.WHITE)
                 .append(new TextComponent("Wave ").withStyle(ChatFormatting.WHITE))
                 .append(new TextComponent((currentWave + 1)
                         + (this.phase == Phase.IN_WAVE || maxWaves <= 0 ? "" : " of " + maxWaves)).withStyle(ChatFormatting.WHITE));
      } else {
         txt = new TextComponent("Complete").withStyle(ChatFormatting.WHITE);
      }

      double progress = this.villagerMaxHealth <= 0.0 ? 0.0 : this.villagerHealth / this.villagerMaxHealth;
      this.renderProgressBar(context, HUD, progress, txt);
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
   public Phase getPhase() {
      return this.phase;
   }

   @Generated
   public void setPhase(Phase phase) {
      this.phase = phase;
   }

   private static void forceBlockSync(ServerLevel world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      for (ServerPlayer player : world.players()) {
         player.connection.send(new ClientboundBlockUpdatePacket(world, pos));
         if (blockEntity != null) {
            player.connection.send(ClientboundBlockEntityDataPacket.create(blockEntity));
         }
      }
   }

   public static enum Phase {
      IDLE,
      STARTING,
      PROXIES,
      IN_WAVE,
      FORFEITED,
      FAILED,
      COMPLETED;
   }
}
