package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.client.gui.screen.player.legacy.widget.SkillWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.TalentTree;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = TalentWidget.class, remap = false)
public abstract class MixinTalentWidget extends SkillWidget<TalentTree> {
    public MixinTalentWidget(TalentTree tree, Component message, TieredSkill skill, SkillStyle style) {
        super(tree, message, skill, style);
    }

    // Keep the new talent layout, tooltips and build previews while displaying bonus ranks.
    @ModifyVariable(method = "renderPips", at = @At("STORE"), ordinal = 0)
    private String woldsvaults$showBonusRanks(String text) {
        TieredSkill skill = getSkill();
        int bonus = skill.getActualTier() - skill.getUnmodifiedTier();
        if (bonus == 0) return text;
        return skill.getUnmodifiedTier() + (bonus > 0 ? "+" : "") + bonus + "/" + skill.getMaxLearnableTier();
    }
}