package xyz.iwolfking.woldsvaults.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import iskallia.vault.init.ModSounds;

public class WitchskallEntity extends VaultNecromancer {
    public WitchskallEntity(EntityType<? extends Evoker> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WITCHSKALL_IDLE;
    }
}
