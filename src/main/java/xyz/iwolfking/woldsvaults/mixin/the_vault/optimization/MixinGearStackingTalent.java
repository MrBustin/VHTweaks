package xyz.iwolfking.woldsvaults.mixin.the_vault.optimization;

import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.talent.type.GearAttributeTalent;
import iskallia.vault.skill.talent.type.StackingGearAttributeTalent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = StackingGearAttributeTalent.class, remap = false)
public class MixinGearStackingTalent extends GearAttributeTalent {
    @Shadow
    private int timeLeft;
    @Shadow private int stacks;
    @Shadow private MobEffect effect;

    /**
     * @author iwolfking
     * @reason Holy shit why lol
     */
    @Overwrite
    public void onTick(SkillContext context) {
        if (--this.timeLeft < 0 && this.isUnlocked() && this.stacks > 0) {
            this.timeLeft = 0;
            this.stacks = 0;
            context.getSource().as(ServerPlayer.class).ifPresent(this::refreshSnapshot);
        }

        super.onTick(context);
        if (this.isUnlocked() && this.effect != null && this.timeLeft > 0 && this.stacks > 0) {
            context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
                player.removeEffect(this.effect);
                player.addEffect(new MobEffectInstance(this.effect, this.timeLeft, this.stacks - 1, true, false, true));
            });
        }

    }
}
