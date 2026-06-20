package xyz.iwolfking.woldsvaults.entities.ghosts;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.MagicWraith;


public class DarkBlueGhost extends MagicWraith {
    public DarkBlueGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn,0.2f);
    }
}
