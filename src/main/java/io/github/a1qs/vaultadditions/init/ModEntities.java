package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.entity.IdonasBarrageArrowEntity;
import io.github.a1qs.vaultadditions.entity.IdonasBarrageEntity;
import io.github.a1qs.vaultadditions.entity.ThornCloudEntity;
import iskallia.vault.VaultMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.RegistryEvent;

public class ModEntities {
    public static EntityType<ThornCloudEntity> THORN_CLOUD;
    public static EntityType<IdonasBarrageArrowEntity> IDONAS_BARRAGE_ARROW;
    public static EntityType<IdonasBarrageEntity> IDONAS_BARRAGE;

    public static void register(RegistryEvent.Register<EntityType<?>> event) {
        THORN_CLOUD = register("thorn_cloud", EntityType.Builder.of(ThornCloudEntity::new, MobCategory.MISC), event);
        IDONAS_BARRAGE_ARROW = register("idonas_barrage_arrow", EntityType.Builder.<IdonasBarrageArrowEntity>of(IdonasBarrageArrowEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20), event);
        IDONAS_BARRAGE = register("idonas_barrage", EntityType.Builder.<IdonasBarrageEntity>of(IdonasBarrageEntity::new, MobCategory.MISC)
                .sized(8.0F, 0.5F)
                .clientTrackingRange(8)
                .updateInterval(2), event);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder, RegistryEvent.Register<EntityType<?>> event) {
        EntityType<T> entityType = builder.build(VaultAdditions.MOD_ID + ":" + name);
        event.getRegistry().register(entityType.setRegistryName(VaultMod.id(name)));
        return entityType;
    }
}
