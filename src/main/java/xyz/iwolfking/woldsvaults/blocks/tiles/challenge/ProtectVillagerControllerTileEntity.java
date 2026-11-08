package xyz.iwolfking.woldsvaults.blocks.tiles.challenge;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerChallengeManager;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.core.world.storage.BlockCuboid;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ProtectVillagerControllerTileEntity extends ChallengeControllerBlockEntity<ProtectVillagerChallengeManager> {
   private BlockCuboid zone;
   private final List<EntityType<?>> mobTypes;
   private IntRoll waves;
   private double baseMobHealth;
   private final List<ChallengeAction<?>> successActions;
   private final List<ChallengeAction<?>> failureActions;
   private final List<ChallengeAction<?>> gildedActions;
   private final List<ChallengeAction<?>> ornateActions;
   private final List<ChallengeAction<?>> livingActions;
   private final List<ChallengeAction<?>> coinPileActions;
   private final List<ChallengeAction<?>> floatingItemActions;
   private final Map<BlockPos, Rotation> gates;
   public static final ArrayAdapter<ResourceLocation> IDENTIFIERS = Adapters.ofArray(ResourceLocation[]::new, Adapters.IDENTIFIER);
   public static final ArrayAdapter<ChallengeAction<?>> ACTIONS = Adapters.ofArray(ChallengeAction[]::new, Adapters.CHALLENGE_ACTION);

   public ProtectVillagerControllerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY, pos, state);
      this.setRenderer(40960, 40960, 40960);
      this.zone = null;
      this.mobTypes = new ArrayList<>();
      this.waves = IntRoll.ofUniform(4, 8);
      this.successActions = new ArrayList<>();
      this.failureActions = new ArrayList<>();
      this.gildedActions = new ArrayList<>();
      this.ornateActions = new ArrayList<>();
      this.livingActions = new ArrayList<>();
      this.coinPileActions = new ArrayList<>();
      this.floatingItemActions = new ArrayList<>();
      this.gates = new LinkedHashMap<>();
      // The Vault base class only initializes its curses list during load().
      this.load(new CompoundTag());
   }

   public ProtectVillagerChallengeManager createManager() {
      ProtectVillagerChallengeManager manager = new ProtectVillagerChallengeManager(this.uuid, this.level.dimension(), this.getBlockPos());
      manager.setZone(this.zone);
      manager.setMobTypes(this.mobTypes);
      manager.setBaseMobHealth(this.baseMobHealth);
      manager.setMaxWaves(this.waves.get(JavaRandom.ofInternal(this.level.random.nextLong())));
      manager.addSuccessActions(this.successActions);
      manager.addFailureActions(this.failureActions);
      manager.setRewardActions("Gilded", this.gildedActions);
      manager.setRewardActions("Ornate", this.ornateActions);
      manager.setRewardActions("Living", this.livingActions);
      manager.setRewardActions("Coin Piles", this.coinPileActions);
      manager.setFloatingItemActions(this.floatingItemActions);
      this.gates.forEach(manager::addGate);
      return manager;
   }

   public void setZone(BlockCuboid zone) {
      this.zone = zone;
      this.sendUpdates();
   }

   public void setMobTypes(List<EntityType<?>> mobTypes) {
      this.mobTypes.clear();
      this.mobTypes.addAll(mobTypes);
      this.sendUpdates();
   }

   public void setWaves(IntRoll waves) {
      this.waves = waves;
      this.sendUpdates();
   }

   public void setGildedActions(List<ChallengeAction<?>> actions) {
      this.replaceActions(this.gildedActions, actions);
   }

   public void setOrnateActions(List<ChallengeAction<?>> actions) {
      this.replaceActions(this.ornateActions, actions);
   }

   public void setLivingActions(List<ChallengeAction<?>> actions) {
      this.replaceActions(this.livingActions, actions);
   }

   public void setCoinPileActions(List<ChallengeAction<?>> actions) {
      this.replaceActions(this.coinPileActions, actions);
   }

   public void setFloatingItemActions(List<ChallengeAction<?>> actions) {
      this.replaceActions(this.floatingItemActions, actions);
   }

   private void replaceActions(List<ChallengeAction<?>> destination, List<ChallengeAction<?>> actions) {
      destination.clear();
      destination.addAll(actions);
      this.sendUpdates();
   }

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
      IDENTIFIERS.writeNbt(this.mobTypes.stream().map(ForgeRegistryEntry::getRegistryName).toArray(ResourceLocation[]::new))
         .ifPresent(tag -> nbt.put("mobTypes", tag));
      Adapters.INT_ROLL.writeNbt(this.waves).ifPresent(tag -> nbt.put("waves", tag));
      Adapters.DOUBLE.writeNbt(this.baseMobHealth).ifPresent(tag -> nbt.put("baseModHealth", tag));
      ACTIONS.writeNbt(this.successActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("successActions", tag));
      ACTIONS.writeNbt(this.failureActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("failureActions", tag));
      ACTIONS.writeNbt(this.gildedActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("gildedActions", tag));
      ACTIONS.writeNbt(this.ornateActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("ornateActions", tag));
      ACTIONS.writeNbt(this.livingActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("livingActions", tag));
      ACTIONS.writeNbt(this.coinPileActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("coinPileActions", tag));
      ACTIONS.writeNbt(this.floatingItemActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("floatingItemActions", tag));
      ListTag gatesTag = new ListTag();
      this.gates.forEach((pos, rotation) -> {
         CompoundTag gate = new CompoundTag();
         Adapters.BLOCK_POS.writeNbt(pos).ifPresent(value -> gate.put("pos", value));
         Adapters.INT.writeNbt(rotation.ordinal()).ifPresent(value -> gate.put("rotation", value));
         gatesTag.add(gate);
      });
      nbt.put("gates", gatesTag);
   }

   @Override
   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
      this.mobTypes.clear();
      ResourceLocation[] ids = IDENTIFIERS.readNbt(nbt.get("mobTypes")).orElse(new ResourceLocation[0]);

      for (ResourceLocation id : ids) {
         EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(id);
         if (type != null) {
            this.mobTypes.add(type);
         }
      }

      this.waves = Adapters.INT_ROLL.readNbt(nbt.get("waves")).orElse(IntRoll.ofUniform(4, 8));
      this.baseMobHealth = Adapters.DOUBLE.readNbt(nbt.get("baseModHealth")).orElse(100.0);
      this.successActions.clear();
      ACTIONS.readNbt(nbt.get("successActions")).ifPresent(actions -> this.successActions.addAll(Arrays.asList(actions)));
      this.failureActions.clear();
      ACTIONS.readNbt(nbt.get("failureActions")).ifPresent(actions -> this.failureActions.addAll(Arrays.asList(actions)));
      this.gildedActions.clear();
      ACTIONS.readNbt(nbt.get("gildedActions")).ifPresent(actions -> this.gildedActions.addAll(Arrays.asList(actions)));
      this.ornateActions.clear();
      ACTIONS.readNbt(nbt.get("ornateActions")).ifPresent(actions -> this.ornateActions.addAll(Arrays.asList(actions)));
      this.livingActions.clear();
      ACTIONS.readNbt(nbt.get("livingActions")).ifPresent(actions -> this.livingActions.addAll(Arrays.asList(actions)));
      this.coinPileActions.clear();
      ACTIONS.readNbt(nbt.get("coinPileActions")).ifPresent(actions -> this.coinPileActions.addAll(Arrays.asList(actions)));
      this.floatingItemActions.clear();
      ACTIONS.readNbt(nbt.get("floatingItemActions")).ifPresent(actions -> this.floatingItemActions.addAll(Arrays.asList(actions)));
      this.gates.clear();
      ListTag gatesTag = nbt.getList("gates", Tag.TAG_COMPOUND);
      for (int i = 0; i < gatesTag.size(); i++) {
         CompoundTag gate = gatesTag.getCompound(i);
         BlockPos gatePos = Adapters.BLOCK_POS.readNbt(gate.get("pos")).orElse(null);
         int rotation = Adapters.INT.readNbt(gate.get("rotation")).orElse(0);
         if (gatePos != null) {
            Rotation[] rotations = Rotation.values();
            this.gates.put(gatePos, rotations[Math.floorMod(rotation, rotations.length)]);
         }
      }
   }
}
