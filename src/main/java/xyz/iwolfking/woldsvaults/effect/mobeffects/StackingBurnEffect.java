package xyz.iwolfking.woldsvaults.effect.mobeffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import xyz.iwolfking.woldsvaults.init.ModEffects;

/** A separate burn prevents Fire Nova and burning-hit gear from replacing aura stacks. */
public class StackingBurnEffect extends PercentBurnEffect {
    private static final String STATE = "ImmolationBurn";
    public static final int LINGER_TICKS = 40;

    public StackingBurnEffect() {
        super("stacking_burn");
    }

    @Override
    protected CompoundTag getBurnData(LivingEntity target) {
        return target.getPersistentData().getCompound(STATE);
    }

    @Override
    protected float getDamage(CompoundTag data, int amplifier) {
        return readState(data).damagePerTick(data.getFloat(BURN_ATTACK_SNAPSHOT));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Duration is refreshed in the aura; the independent timer controls damage.
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        if (target.level.isClientSide) {
            return;
        }

        CompoundTag data = getBurnData(target);
        BurnStackState state = readState(data);
        long tick = target.level.getGameTime();
        if (state.stacks() <= 0 || !state.isDamageDue(tick)) {
            return;
        }

        writeState(data, state.afterDamage(tick));
        super.applyEffectTick(target, amplifier);
    }

    public static void apply(LivingEntity target, float damagePerStack, LivingEntity attacker) {
        if (target.level.isClientSide || !target.isAlive()) {
            return;
        }

        long tick = target.level.getGameTime();
        CompoundTag data = target.hasEffect(ModEffects.STACKING_BURN)
                ? target.getPersistentData().getCompound(STATE) : new CompoundTag();
        BurnStackState previous = readState(data);
        BurnStackState state = previous.stacks() > 0 ? previous.expose(tick) : BurnStackState.start(tick);

        // Within overlapping auras, use the strongest source for this tick.
        if (previous.stacks() == 0 || previous.lastExposureTick() != tick
                || damagePerStack >= data.getFloat(BURN_ATTACK_SNAPSHOT)) {
            data.putFloat(BURN_ATTACK_SNAPSHOT, damagePerStack);
            data.putUUID(BURN_SOURCE_UUID, attacker.getUUID());
        }
        data.putBoolean(BURN_IS_ABILITY_POWER, true);
        writeState(data, state);

        target.addEffect(new MobEffectInstance(ModEffects.STACKING_BURN, LINGER_TICKS, 0, false, true, true));
        if (target.hasEffect(ModEffects.STACKING_BURN)) {
            target.getPersistentData().put(STATE, data);
        }
    }

    private static BurnStackState readState(CompoundTag data) {
        return new BurnStackState(data.getInt("stacks"), data.getInt("exposureTicks"),
                data.getLong("lastExposureTick"), data.getLong("nextDamageTick"));
    }

    private static void writeState(CompoundTag data, BurnStackState state) {
        data.putInt("stacks", state.stacks());
        data.putInt("exposureTicks", state.exposureTicks());
        data.putLong("lastExposureTick", state.lastExposureTick());
        data.putLong("nextDamageTick", state.nextDamageTick());
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        // Minecraft also calls this when an existing effect's duration is refreshed.
        if (!entity.hasEffect(this)) {
            entity.getPersistentData().remove(STATE);
        }
    }
}
