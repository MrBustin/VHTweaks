package xyz.iwolfking.woldsvaults.entities.ghosts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.GenericWraith;


public class RedGhost extends GenericWraith {
    public RedGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn);
    }
}
