package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.BaseSpawnerTileEntity;
import iskallia.vault.block.entity.WildSpawnerTileEntity;
import iskallia.vault.config.WildSpawnerConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(value = WildSpawnerTileEntity.class, remap = false)
public class MixinWildSpawnerTileEntity extends BaseSpawnerTileEntity {
    @Shadow
    @Nullable
    private WildSpawnerConfig.SpawnerGroup spawnerGroup;

    protected MixinWildSpawnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @author iwolfking
     * @reason Spawn enhanced wraiths for spooky Haunted Braziers spawners.
     */
    @Overwrite
    private static void spawnEntity(Level level, BlockPos blockPos, ServerLevel serverLevel, WildSpawnerConfig.SpawnerGroup spawnerGroup) {
        WildSpawnerConfig.SpawnerEntity spawnerEntity = spawnerGroup.entities.getRandom(level.random);
        if (spawnerEntity == null) {
            VaultMod.LOGGER.warn("Wild Spawner failed to spawn as there was no valid entity found in config for spawn group with minLevel {}", spawnerGroup.minLevel);
            return;
        }

        if ("quark:wraith".equals(spawnerEntity.type.toString())) {
            spawnBuffedWraith(blockPos, serverLevel, spawnerEntity.type, spawnerEntity.nbt, false, () ->
                    VaultMod.LOGGER.warn("Wild Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type));
        } else {
            spawnEntity(blockPos, serverLevel, spawnerEntity.type, spawnerEntity.nbt, false, () ->
                    VaultMod.LOGGER.warn("Wild Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type));
        }
    }

    @Unique
    @Nullable
    private static Entity spawnBuffedWraith(BlockPos blockPos, ServerLevel serverLevel, ResourceLocation entityName, @Nullable CompoundTag entityNbt, boolean isPersistent, Runnable logEntityTypeMissing) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityName);
        if (entityType == null) {
            logEntityTypeMissing.run();
            return null;
        }

        Entity entity = entityType.spawn(serverLevel, null, null, blockPos, MobSpawnType.SPAWNER, false, false);
        if (entity == null) {
            return null;
        }

        if (entityNbt != null) {
            CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
            entityTag.merge(entityNbt.copy());
            entity.load(entityTag);
        }

        if (entity instanceof Mob mob) {
            if (isPersistent) {
                mob.setPersistenceRequired();
            }

            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 32567, 10));
        }

        return entity;
    }

    /**
     * @author iwolfking
     * @reason Use the spooky wild spawner group for Haunted Braziers vaults.
     */
    @Overwrite
    private void initSpawnerGroup() {
        if (this.spawnerGroup != null) {
            return;
        }

        int vaultLevel = ServerVaults.get(this.level).map(vault -> vault.get(Vault.LEVEL).get()).orElse(0);
        Vault vault = ServerVaults.get(this.level).orElse(null);
        if (vault == null) {
            return;
        }

        String objective = vault.get(Vault.OBJECTIVES).get(Objectives.KEY);
        boolean hasSpooky = false;

        for (WildSpawnerConfig.SpawnerGroup group : ModConfigs.WILD_SPAWNER.spawnerGroups) {
            if (group.minLevel <= vaultLevel && (this.spawnerGroup == null || group.minLevel > this.spawnerGroup.minLevel)) {
                if (!"haunted_braziers".equals(objective)) {
                    hasSpooky = vault.get(Vault.MODIFIERS).getModifiers().stream()
                            .anyMatch(vaultModifier -> "the_vault:spooky".equals(vaultModifier.getId().toString()));
                }

                if (("haunted_braziers".equals(objective) || hasSpooky) && ModConfigs.WILD_SPAWNER.spawnerGroups.size() > 3) {
                    this.spawnerGroup = ModConfigs.WILD_SPAWNER.spawnerGroups.get(3);
                } else {
                    this.spawnerGroup = group;
                }
            }
        }
    }
}
