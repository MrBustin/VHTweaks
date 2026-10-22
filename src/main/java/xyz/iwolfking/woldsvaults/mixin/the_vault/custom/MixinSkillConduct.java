package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.JavelinConductTalent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.events.WoldActiveFlags;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Skill.class, remap = false)
public abstract class MixinSkillConduct {
    @Unique
    private static final JavelinConductTalent WOLDSVAULTS$GEAR_CONDUCT = createGearConductTalent();

    @Unique
    private static JavelinConductTalent createGearConductTalent() {
        JavelinConductTalent talent = new JavelinConductTalent();
        talent.setPresentSilent(true);
        return talent;
    }

    @Inject(method = "getAll", at = @At("HEAD"), cancellable = true)
    private <T> void supplyGearConductTalent(
            Class<T> type,
            Predicate<T> filter,
            CallbackInfoReturnable<List<T>> cir) {
        if (type == JavelinConductTalent.class && WoldActiveFlags.IS_GEAR_CONDUCTING_JAVELIN.isSet()) {
            T talent = type.cast(WOLDSVAULTS$GEAR_CONDUCT);
            cir.setReturnValue(filter.test(talent) ? List.of(talent) : List.of());
        }
    }
}
