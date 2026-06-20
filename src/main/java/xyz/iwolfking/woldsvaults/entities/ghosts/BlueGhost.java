package xyz.iwolfking.woldsvaults.entities.ghosts;

import iskallia.vault.init.ModEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.GenericEffectWraith;

public class BlueGhost extends GenericEffectWraith {
    public BlueGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn, ModEffects.CHILLED, 100, 2);
    }
}
