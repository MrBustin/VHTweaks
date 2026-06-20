package xyz.iwolfking.woldsvaults.init;

import net.minecraft.world.level.GameRules;
import xyz.iwolfking.woldsvaults.mixin.GameRulesBooleanValueAccessor;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> NORMALIZED_ENABLED;
    public static GameRules.Key<GameRules.BooleanValue> UNLIMITED_ALCHEMY_OVERSTACKING;

    public static void initialize() {
        NORMALIZED_ENABLED = GameRules.register("enableDifficultyLockModifiers", GameRules.Category.PLAYER, GameRulesBooleanValueAccessor.create(true));
        UNLIMITED_ALCHEMY_OVERSTACKING = GameRules.register("unlimitedAlchemyOverflow", GameRules.Category.PLAYER, GameRulesBooleanValueAccessor.create(false));
    }
}
