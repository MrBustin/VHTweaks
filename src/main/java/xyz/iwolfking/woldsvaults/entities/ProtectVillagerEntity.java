package xyz.iwolfking.woldsvaults.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

/** A stationary, oversized villager used by the Protect Villager challenge. */
public class ProtectVillagerEntity extends Villager {
    public ProtectVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
    }
}
