package xyz.iwolfking.woldsvaults.entities.ghosts;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.GenericEffectWraith;

public class GreenGhost extends GenericEffectWraith {
    public GreenGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn, MobEffects.POISON, 200, 1);
    }
}
