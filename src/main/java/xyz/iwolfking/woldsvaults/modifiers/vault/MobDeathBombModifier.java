package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.CustomEntitySpawnerConfig;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class MobDeathBombModifier  extends VaultModifier<MobDeathBombModifier.Properties> {
    public MobDeathBombModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
    }

    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
        CommonEvents.ENTITY_DEATH.register(context.getUUID(), EventPriority.HIGHEST, (event) -> {
            if (event.getEntity().level == world) {
                Entity spawnedEntity = event.getEntity();
                if (spawnedEntity instanceof LivingEntity entity) {
                    if (properties().getShouldFilterExclude() && (this.properties).filter.test(entity)) {
                        return;
                    }


                    Random random = entity.level.getRandom();
                    if(random.nextDouble() < this.properties.getChance()) {
                        int amountToSpawn = this.properties.getAmounts().getRandom(random);
                        for(int i = 0; i < amountToSpawn; i++) {
                            EntityType<?> typeToSpawn = ForgeRegistries.ENTITIES.getValue(this.properties.getEntities().getRandom(random).type);
                            if(typeToSpawn != null) {
                                Entity toSpawn = typeToSpawn.create(world);
                                if(toSpawn != null) {
                                    toSpawn.setPos(entity.getEyePosition());
                                    world.addFreshEntity(toSpawn);
                                }

                            }
                        }

                    }
                }
            }
        });
    }

    public static class Properties {
        @Expose
        private final EntityPredicate filter;

        @Expose
        private final boolean filterShouldExclude;
        @Expose
        private final double chance;

        @Expose
        private final WeightedList<Integer> amounts;

        @Expose
        private final WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities;

        public Properties(EntityPredicate filter, boolean filterShouldExclude, double chance, WeightedList<Integer> amounts, WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> entities) {
            this.filter = filter;
            this.filterShouldExclude = filterShouldExclude;
            this.chance = chance;
            this.amounts = amounts;
            this.entities = entities;
        }


        public EntityPredicate getFilter() {
            return this.filter;
        }
        public double getChance() {
            return this.chance;
        }
        public boolean getShouldFilterExclude() {
            return this.filterShouldExclude;
        }
        public WeightedList<Integer> getAmounts() {
            return this.amounts;
        }

        public WeightedList<CustomEntitySpawnerConfig.SpawnerEntity> getEntities() {
            return this.entities;
        }

    }
}
