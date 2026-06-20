package io.github.a1qs.vaultadditions.vault.menu;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.Configs;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.SkillPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentGroupWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.TieredSkill;
import net.minecraft.network.chat.TextComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerPanRegion extends SkillPanRegion<PowerTree, PowerElementContainerScreen, PowerWidget> {

    public PowerPanRegion(PowerDialog powerDialog, PowerElementContainerScreen parentScreen) {
        super(parentScreen, new TextComponent("Talents Tab"), powerDialog);
    }

    @Override
    protected void initSkillWidget(PowerTree skillTree, String skillName, SkillStyle style, Map<String, PowerWidget> widgets, List<TalentGroupWidget> groups) {
        Skill skill = skillTree.getForId(skillName).orElse(null);
        if (skill instanceof TieredSkill tieredSkill) {
            widgets.put(skillName, new PowerWidget(tieredSkill, skillTree, style));
        } else {
            VaultAdditions.LOGGER.error("Power skill {} is not a TieredSkill", skillName);
        }
    }

    @Override
    protected HashMap<String, SkillStyle> getStyles() {
        return Configs.POWERS_GUI.getStyles();
    }
}
