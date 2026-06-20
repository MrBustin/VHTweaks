package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.CustomEntitySpawnerConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;

public class RetroSpawnVaultModifier extends VaultModifier<RetroSpawnVaultModifier.Properties> {

    public RetroSpawnVaultModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
    }

    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
        CommonEvents.PLAYER_TICK.register(context.getUUID(), EventPriority.HIGHEST, (event) -> {
            if(event.side.isServer()) {
                if(!((event.player.tickCount % this.properties.getTicksPerCheck()) == 0)) {
                    return;
                }

                if(vault.get(Vault.CLOCK).has(TickClock.PAUSED)) {
                    return;
                }

                if(event.player.getRandom().nextDouble() < this.properties.getChance()) {
                    if(!(event.player.getLevel().dimension().equals(world.dimension()))) {
                        return;
                    }

                    for(int i = 0; i < this.properties.amounts.getRandom(event.player.getRandom()); i++) {
                        int livingEntitiesAroundCount = world.getEntitiesOfClass(LivingEntity.class, new AABB(event.player.getOnPos()).inflate(48)).size();

                        if(this.properties.cap < livingEntitiesAroundCount) {
                            return;
                        }

                        doSpawn((VirtualWorld) event.player.level, event.player.getOnPos(), (Random) event.player.getRandom());
                    }
                }
            }
        });
    }

    public static class Properties {
        @Expose
        private final double chance;
        @Expose
        private final int ticksPerCheck;

        @Expose
        private final WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities;

        @Expose
        private final WeightedList<Integer> amounts;

        @Expose
        private final int cap;

        public Properties(double chance, int ticksPerCheck, WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities, WeightedList<Integer> amounts, int cap) {
            this.chance = chance;
            this.ticksPerCheck = ticksPerCheck;
            this.entities = entities;
            this.amounts = amounts;
            this.cap = cap;
        }



        public double getChance() {
            return this.chance;
        }
        public int getTicksPerCheck() {
            return this.ticksPerCheck;
        }
        public int getCap() {
            return this.cap;
        }
        public WeightedList<Integer>  getAmounts() {
            return this.amounts;
        }
        public WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> getEntities() {
            return this.entities;
        }

    }

    public LivingEntity doSpawn(VirtualWorld world, BlockPos pos, Random random) {
        double min = 20.0;
        double max = 30.0;

        LivingEntity spawned;
        int x;
        int z;
        int y;

        for(spawned = null; spawned == null; spawned = spawnMob(world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, random)) {
            double angle = 6.283185307179586 * random.nextDouble();
            double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
            x = (int)Math.ceil(distance * Math.cos(angle));
            z = (int)Math.ceil(distance * Math.sin(angle));
            double xzRadius = Math.sqrt((double)(x * x + z * z));
            double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
            y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);
        }

        return spawned;
    }

    @Nullable
    public LivingEntity spawnMob(VirtualWorld world, int x, int y, int z, Random random) {
        Entity entity;
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(this.properties.getEntities().getRandom(random).type);


        if(type != null) {
            entity = type.create(world);
        } else {
            entity = null;
        }

        BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
        if (!state.isValidSpawn(world, new BlockPos(x, y - 1, z), entity.getType())) {
            return null;
        } else {
            AABB entityBox = entity.getType().getAABB((double)x + 0.5, (double)y, (double)z + 0.5);
            if (!world.noCollision(entityBox)) {
                return null;
            } else {
                entity.moveTo((double)((float)x + 0.5F), (double)((float)y + 0.2F), (double)((float)z + 0.5F), (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
                world.addWithUUID(entity);
                return (LivingEntity) entity;
            }
        }
    }
}
