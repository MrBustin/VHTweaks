package xyz.iwolfking.woldsvaults.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanValue.class)
public interface GameRulesBooleanValueAccessor {
    @Invoker("create")
    static GameRules.Type<GameRules.BooleanValue> create(boolean defaultValue) {
        throw new AssertionError();
    }
}
