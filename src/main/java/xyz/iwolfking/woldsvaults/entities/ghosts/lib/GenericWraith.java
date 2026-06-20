package xyz.iwolfking.woldsvaults.entities.ghosts.lib;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;

public class GenericWraith extends Wraith {

    public GenericWraith(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn);
    }

}
