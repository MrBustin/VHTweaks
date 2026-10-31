package xyz.iwolfking.woldsvaults.effect.mobeffects;

/** Timing for Immolation, independent of effect-duration refreshes. */
public record BurnStackState(int stacks, int exposureTicks, long lastExposureTick, long nextDamageTick) {
    public static final int STACK_INTERVAL_TICKS = 20;
    public static final int DAMAGE_INTERVAL_TICKS = 20;

    public static BurnStackState start(long tick) {
        return new BurnStackState(1, 0, tick, tick);
    }

    public BurnStackState expose(long tick) {
        // Overlapping auras must not grant multiple stacks in the same second.
        if (tick == lastExposureTick) {
            return this;
        }

        int progress = tick == lastExposureTick + 1 ? exposureTicks + 1 : 0;
        if (progress >= STACK_INTERVAL_TICKS) {
            return new BurnStackState(stacks == Integer.MAX_VALUE ? stacks : stacks + 1,
                    0, tick, nextDamageTick);
        }
        return new BurnStackState(stacks, progress, tick, nextDamageTick);
    }

    public boolean isDamageDue(long tick) {
        return tick >= nextDamageTick;
    }

    public float damagePerTick(float damagePerStack) {
        return damagePerStack * stacks;
    }

    public BurnStackState afterDamage(long tick) {
        return new BurnStackState(stacks, exposureTicks, lastExposureTick, tick + DAMAGE_INTERVAL_TICKS);
    }
}
