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
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ProtectVillagerControllerTileEntity extends ChallengeControllerBlockEntity<ProtectVillagerChallengeManager> {
   private BlockCuboid zone;
   private final List<EntityType<?>> mobTypes;
   private IntRoll waves;
   private final List<ChallengeAction<?>> successActions;
   private final List<ChallengeAction<?>> failureActions;
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
      // The Vault base class only initializes its curses list during load().
      this.load(new CompoundTag());
   }

   public ProtectVillagerChallengeManager createManager() {
      ProtectVillagerChallengeManager manager = new ProtectVillagerChallengeManager(this.uuid, this.level.dimension(), this.getBlockPos());
      manager.setZone(this.zone);
      manager.setMobTypes(this.mobTypes);
      manager.setMaxWaves(this.waves.get(JavaRandom.ofInternal(this.level.random.nextLong())));
      manager.addSuccessActions(this.successActions);
      manager.addFailureActions(this.failureActions);
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

   @Override
   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
      IDENTIFIERS.writeNbt(this.mobTypes.stream().map(ForgeRegistryEntry::getRegistryName).toArray(ResourceLocation[]::new))
         .ifPresent(tag -> nbt.put("mobTypes", tag));
      Adapters.INT_ROLL.writeNbt(this.waves).ifPresent(tag -> nbt.put("waves", tag));
      ACTIONS.writeNbt(this.successActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("successActions", tag));
      ACTIONS.writeNbt(this.failureActions.toArray(ChallengeAction[]::new)).ifPresent(tag -> nbt.put("failureActions", tag));
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
      this.successActions.clear();
      ACTIONS.readNbt(nbt.get("successActions")).ifPresent(actions -> this.successActions.addAll(Arrays.asList(actions)));
      this.failureActions.clear();
      ACTIONS.readNbt(nbt.get("failureActions")).ifPresent(actions -> this.failureActions.addAll(Arrays.asList(actions)));
   }
}
