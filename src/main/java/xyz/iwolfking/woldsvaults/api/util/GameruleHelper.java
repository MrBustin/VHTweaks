package xyz.iwolfking.woldsvaults.api.util;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class GameruleHelper {
    public static boolean isEnabled(GameRules.Key<GameRules.BooleanValue> gamerule, Level level) {
        return level.getGameRules().getBoolean(gamerule);
    }
}
