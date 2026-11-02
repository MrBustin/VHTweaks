package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.serializable.ISerializable;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.challenge.protect.ProtectVillagerTargetGoal;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class ProtectVillagerSpawner implements ISerializable<CompoundTag, JsonObject> {
   private final List<EntityType<?>> mobTypes = new ArrayList<>();
   private final Set<UUID> mobs = new HashSet<>();
   private int wave;
   private int maxWaves;
   private UUID targetVillagerId;

   public void setMobTypes(List<EntityType<?>> types) {
      this.mobTypes.clear();
      this.mobTypes.addAll(types);
   }

   public void setMaxWaves(int maxWaves) {
      this.maxWaves = maxWaves;
   }

   public int getWave() {
      return this.wave;
   }

   public int getMaxWaves() {
      return this.maxWaves;
   }

   public boolean isWaveCleared() {
      return this.mobs.isEmpty();
   }

   public boolean isCompleted() {
      return this.wave >= this.maxWaves && this.mobs.isEmpty();
   }

   public boolean isWaveMob(Entity entity) {
      return entity != null && this.mobs.contains(entity.getUUID());
   }

   public void onAttach(ServerLevel world) {
      CommonEvents.ENTITY_LEAVE.register(this, event -> {
         if (event.getEntity().level == world) {
            this.mobs.remove(event.getEntity().getUUID());
         }
      });
   }

   public void onDetach() {
      CommonEvents.ENTITY_LEAVE.release(this);
   }

   public void onTick(ServerLevel world) {
      this.mobs.removeIf(id -> world.getEntity(id) == null);
   }

   public void setTargetVillager(UUID villagerId) {
      this.targetVillagerId = villagerId;
   }

   public void spawnWave(ServerLevel world, BlockPos pos) {
      this.wave++;
      RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

      // Distance of each corner from the center
      int cornerDistance = 10;

      // Randomly select one of four corners
      int corner = random.nextInt(4);

      int offsetX = (corner == 0 || corner == 2) ? cornerDistance : -cornerDistance;
      int offsetZ = (corner == 0 || corner == 1) ? cornerDistance : -cornerDistance;

      // Center position of the selected corner
      BlockPos spawnCenter = pos.offset(offsetX, 0, offsetZ);

      // Random spread around the selected corner
      double spread = 4.0;

      for (EntityType<?> type : this.mobTypes) {
         for (int amount = 0; amount <= this.mobTypes.size() * wave; amount++){
            Entity entity = type.create(world);

            if (entity != null) {
               double x = spawnCenter.getX() + 0.5 + (random.nextDouble() - 0.5) * spread;

               double y = pos.getY() + 1;

               double z = spawnCenter.getZ() + 0.5 + (random.nextDouble() - 0.5) * spread;

               entity.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);

               if (entity instanceof Mob mob) {
                  if (this.targetVillagerId != null) {
                     mob.targetSelector.addGoal(0, new ProtectVillagerTargetGoal(mob, this.targetVillagerId, 0.0F));
                  }
               }

               world.addFreshEntity(entity);
               this.mobs.add(entity.getUUID());
            }
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ListTag types = new ListTag();

         for (EntityType<?> type : this.mobTypes) {
            ResourceLocation rl = ForgeRegistries.ENTITIES.getKey(type);
            if (rl != null) {
               types.add(StringTag.valueOf(rl.toString()));
            }
         }

         nbt.put("mobs", types);
         Adapters.INT.writeNbt(Integer.valueOf(this.wave)).ifPresent(tag -> nbt.put("wave", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.maxWaves)).ifPresent(tag -> nbt.put("maxWaves", tag));
         ListTag ids = new ListTag();

         for (UUID id : this.mobs) {
            ids.add(StringTag.valueOf(id.toString()));
         }

         nbt.put("active", ids);
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.mobTypes.clear();
      Tag i = nbt.get("mobs");
      if (i instanceof ListTag) {
         for (Tag tag : (ListTag)i) {
            ResourceLocation rl = ResourceLocation.tryParse(tag.getAsString());
            if (rl != null) {
               EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(rl);
               if (type != null) {
                  this.mobTypes.add(type);
               }
            }
         }
      }

      this.wave = Adapters.INT.readNbt(nbt.get("wave")).orElse(0);
      this.maxWaves = Adapters.INT.readNbt(nbt.get("maxWaves")).orElse(0);
      this.mobs.clear();
      if (nbt.get("active") instanceof ListTag list2) {
         for (int ix = 0; ix < list2.size(); ix++) {
            this.mobs.add(UUID.fromString(list2.getString(ix)));
         }
      }
   }
}
