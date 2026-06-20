package io.github.a1qs.vaultadditions.vault.menu;

import iskallia.vault.client.gui.screen.player.legacy.widget.SkillWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.TieredSkill;
import net.minecraft.network.chat.TextComponent;

public class PowerWidget extends SkillWidget<PowerTree> {

    public PowerWidget(TieredSkill skill, PowerTree powerTree, SkillStyle style) {
        super(powerTree, new TextComponent("the_vault.widgets.power"), skill, style); //todo: change to own modname?
    }

}
