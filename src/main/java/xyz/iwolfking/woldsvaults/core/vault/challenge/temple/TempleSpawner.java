package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.serializable.ISerializable;
import iskallia.vault.core.vault.challenge.base.ChallengeSpawner;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.entity.boss.MobSpawningUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TempleSpawner extends ChallengeSpawner {

    private final List<Spawn> spawns = new ArrayList<>();
    private TempleChallengeManager manager;

    public void addSpawn(PartialEntity entity, int count, String spawner) {
        this.spawns.add(new Spawn(entity, count));
    }

    public boolean hasSpawns() {
        return this.spawns.stream().anyMatch(s -> s.count > 0);
    }

    @Override
    public boolean isCompleted() {
        return this.isTrackingEmpty();
    }

    public void onAttach(ServerLevel world, TempleChallengeManager manager) {
        this.manager = manager;
        this.registerTrackedLeave(world, EventPriority.HIGHEST);

        iskallia.vault.core.event.CommonEvents.ENTITY_TICK.register(this, EventPriority.HIGHEST, event -> {
            LivingEntity entity = event.getEntityLiving();
            if (entity.level != world || entity.level.isClientSide) return;
            if (!this.getTrackedEntityIds().contains(entity.getUUID())) return;
            this.handleTrackedMob(entity);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.manager = null;
    }

    public void onTick(ServerLevel world) {
        this.removeInvalidTrackedEntities(world);
        this.updateTrackedHealth(world);
    }

    public void spawn(ServerLevel world, BlockPos controllerPos, List<BlockPos> spawnPositions) {
        this.resetTrackedHealth();

        this.withTrackedEntityCapture(() -> {
            for (Spawn spawn : this.spawns) {
                if (spawn.entity == null || spawn.count <= 0) continue;

                ResourceLocation id = spawn.entity.getId();
                if (id == null) continue;

                EntityType<?> type = ForgeRegistries.ENTITIES.getValue(id);
                if (type == null) {
                    VaultMod.LOGGER.error("Unable to find entity type {} for temple spawn", id);
                    continue;
                }

                for (int i = 0; i < spawn.count; i++) {
                    BlockPos spawnPos = null;

                    if (!spawnPositions.isEmpty()) {
                        BlockPos offset = spawnPositions.get(world.random.nextInt(spawnPositions.size()));
                        spawnPos = offset.offset(controllerPos);
                    }

                    if (spawnPos == null && this.manager != null && this.manager.getZone() != null) {
                        BlockCuboid zone = this.manager.getZone().offset(controllerPos);
                        spawnPos = MobSpawningUtils.getRandomSpawnPos(world, type, zone, true);
                    }

                    if (spawnPos == null) {
                        VaultMod.LOGGER.error("Failed to find spawn position for {}", id);
                        continue;
                    }

                    Entity entity = type.create(world);
                    if (entity == null) {
                        VaultMod.LOGGER.error("Unable to create entity of type {}", id);
                        continue;
                    }

                    spawn.entity.getNbt().asWhole().ifPresent(entity::load);
                    entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                            world.random.nextFloat() * 360.0F, 0.0F);
                    world.addFreshEntity(entity);
                    this.trackEntity(entity);
                }
            }
        });
    }

    private void handleTrackedMob(LivingEntity entity) {
        if (this.manager == null) return;
        BlockCuboid zone = this.manager.getZone();
        if (zone == null) return;

        BlockCuboid worldZone = zone.offset(this.manager.getPos());
        if (!(entity instanceof Mob mob)) return;

        if (!worldZone.contains(mob.blockPosition())) {
            BlockPos clampedPos = new BlockPos(
                    Mth.clamp(Mth.floor(mob.getX()), worldZone.getMinX(), worldZone.getMaxX()),
                    Mth.clamp(Mth.floor(mob.getY()), worldZone.getMinY(), worldZone.getMaxY()),
                    Mth.clamp(Mth.floor(mob.getZ()), worldZone.getMinZ(), worldZone.getMaxZ())
            );
            mob.teleportTo(clampedPos.getX() + 0.5D, clampedPos.getY(), clampedPos.getZ() + 0.5D);
            mob.setDeltaMovement(Vec3.ZERO);
            mob.getNavigation().stop();
            mob.fallDistance = 0.0F;
        }

        LivingEntity target = mob.getTarget();
        if (target == null) return;
        if (!(target instanceof Player player) || !this.manager.hasPlayer(player) || !worldZone.contains(player.blockPosition())) {
            mob.setTarget(null);
            mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            mob.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            ListTag list = new ListTag();
            for (Spawn spawn : this.spawns) {
                spawn.writeNbt().ifPresent(list::add);
            }
            nbt.put("spawns", list);
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.spawns.clear();
        ListTag list = nbt.getList("spawns", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            Spawn spawn = new Spawn();
            spawn.readNbt(list.getCompound(i));
            this.spawns.add(spawn);
        }
    }

    private static class Spawn implements ISerializable<CompoundTag, JsonObject> {
        private PartialEntity entity;
        private int count;

        Spawn() {}

        Spawn(PartialEntity entity, int count) {
            this.entity = entity;
            this.count = count;
        }

        @Override
        public Optional<CompoundTag> writeNbt() {
            return Optional.of(new CompoundTag()).map(nbt -> {
                Adapters.PARTIAL_ENTITY.writeNbt(this.entity).ifPresent(tag -> nbt.put("entity", tag));
                Adapters.INT.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(CompoundTag nbt) {
            this.entity = Adapters.PARTIAL_ENTITY.readNbt(nbt.get("entity")).orElse(null);
            this.count = Adapters.INT.readNbt(nbt.get("count")).orElse(0);
        }
    }
}
