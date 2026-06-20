package xyz.iwolfking.woldsvaults.objectives.data.lib.events;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import xyz.iwolfking.woldsvaults.objectives.data.EnchantedEventsRegistry;
import xyz.iwolfking.woldsvaults.objectives.lib.BasicEnchantedEvent;


import javax.annotation.Nullable;

public class SpawnEntityEnchantedEvent extends BasicEnchantedEvent {
    private static final int MAX_SPAWN_ATTEMPTS = 64;

    @Expose
    private final WeightedList<EntityType<?>> entities;
    @Expose
    private final WeightedList<Integer> amounts;
    @Expose
    private final WeightedList<MobEffectInstance> effects;

    private final ItemStack heldStack;


    public SpawnEntityEnchantedEvent(String eventName, String eventDescription, String primaryColor, WeightedList<EntityType<?>> entities, WeightedList<Integer> amounts) {
        super(eventName, eventDescription, primaryColor);
        this.entities = entities;
        this.amounts = amounts;
        this.effects = new WeightedList<MobEffectInstance>();
        this.heldStack = ItemStack.EMPTY;
    }

    public SpawnEntityEnchantedEvent(String eventName, String eventDescription, String primaryColor, WeightedList<EntityType<?>> entities, WeightedList<Integer> amounts, WeightedList<MobEffectInstance> appliedEffects) {
        super(eventName, eventDescription, primaryColor);
        this.entities = entities;
        this.amounts = amounts;
        this.effects = appliedEffects;
        this.heldStack = ItemStack.EMPTY;
    }

    public SpawnEntityEnchantedEvent(String eventName, String eventDescription, String primaryColor, WeightedList<EntityType<?>> entities, WeightedList<Integer> amounts, ItemStack stack) {
        super(eventName, eventDescription, primaryColor);
        this.entities = entities;
        this.amounts = amounts;
        this.effects = new WeightedList<MobEffectInstance>();
        this.heldStack = stack;
    }

    @Override
    public void triggerEvent(BlockPos pos, ServerPlayer player, Vault vault) {
        JavaRandom javaRandom = JavaRandom.ofNanoTime();
        int amount = amounts.getRandom().orElse(0);
        for(int i = 0; i < amount; i++) {
            doSpawn((VirtualWorld) player.level, pos, javaRandom);
        }

        if(this.getEventName().equals("La Cucaracha")) {
            EnchantedEventsRegistry.LA_CUCARACHA_RANDOM_EVENT.triggerEvent(pos, player, vault);
        }

        super.triggerEvent(pos, player, vault);
    }

    @Nullable
    public Entity doSpawn(VirtualWorld world, BlockPos pos, RandomSource random) {
        double min = 10.0;
        double max = 13.0;

        for(int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            double angle = 6.283185307179586 * random.nextDouble();
            double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
            int x = (int)Math.ceil(distance * Math.cos(angle));
            int z = (int)Math.ceil(distance * Math.sin(angle));
            double xzRadius = Math.sqrt((double)(x * x + z * z));
            double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
            int y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);

            Entity spawned = spawnMob(world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, random);
            if(spawned != null) {
                return spawned;
            }
        }

        return null;
    }

    @Nullable
    public Entity spawnMob(VirtualWorld world, int x, int y, int z, RandomSource random) {
        EntityType<?> type = entities.getRandom().orElse(null);
        if(type == null) {
            return null;
        }

        Entity entity = type.create(world);
        if(entity == null) {
            return null;
        }

        BlockPos floorPos = new BlockPos(x, y - 1, z);
        BlockState state = world.getBlockState(floorPos);
        if (!state.isValidSpawn(world, floorPos, type)) {
            return null;
        } else {
            AABB entityBox = type.getAABB((double)x + 0.5, (double)y, (double)z + 0.5);
            if (!world.noCollision(entityBox)) {
                return null;
            } else {
                entity.moveTo((double)((float)x + 0.5F), (double)((float)y + 0.2F), (double)((float)z + 0.5F), (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
               // entity.finalizeSpawn(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
                if(!heldStack.equals(ItemStack.EMPTY)) {
                    if(entity instanceof EntityCockroach roach) {
                        roach.setMaracas(true);
                    }
                    else {
                        entity.setItemSlot(EquipmentSlot.MAINHAND, heldStack);
                    }

                }
                if(!effects.isEmpty() && entity instanceof LivingEntity livingEntity) {
                    effects.forEach((mobEffectInstance, aDouble) -> {
                        livingEntity.addEffect(mobEffectInstance);
                    });
                }
                world.addWithUUID(entity);

                return entity;
            }
        }
    }
}
