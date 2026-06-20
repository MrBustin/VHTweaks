package xyz.iwolfking.woldsvaults.entities.ghosts;

import iskallia.vault.init.ModEffects;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.MultiGenericEffectWraith;

public class BlackGhost extends MultiGenericEffectWraith {

    private static final WeightedList<MobEffectInstance> BLACK_GHOST_EFFECTS = new WeightedList<>();

    static {
        BLACK_GHOST_EFFECTS.add(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0), 8);
        BLACK_GHOST_EFFECTS.add(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 8);
        BLACK_GHOST_EFFECTS.add(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 3), 8);
        BLACK_GHOST_EFFECTS.add(new MobEffectInstance(MobEffects.WITHER, 120, 1), 8);
        BLACK_GHOST_EFFECTS.add(new MobEffectInstance(ModEffects.TIMER_ACCELERATION, 60, 1), 8);
    }

    public BlackGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn, BLACK_GHOST_EFFECTS);
    }
}
