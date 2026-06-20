package xyz.iwolfking.woldsvaults.client.menu;

import xyz.iwolfking.woldsvaults.configs.Configs;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.PowerLevelMessage;
import xyz.iwolfking.woldsvaults.util.MiscUtil;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.SkillDialog;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.source.SkillSource;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

public class PowerDialog extends SkillDialog<PowerTree, PowerElementContainerScreen> {

    public PowerDialog(PowerTree powerTree, PowerElementContainerScreen powerElementContainerScreen) {
        super(powerTree, powerElementContainerScreen);
    }

    protected int getUnspentSkillPoints() {
        return MiscUtil.unspentPowerPoints;
    }

    protected void updateRegretButton() {
        this.regretButton = null;
    }

    protected HashMap<String, SkillStyle> getStyles() {
        return Configs.POWERS_GUI.getStyles();
    }

    protected SkillContext getSkillContext() {
        return new SkillContext(VaultBarOverlay.vaultLevel, MiscUtil.unspentPowerPoints, SkillSource.of(Minecraft.getInstance().player));
    }

    protected void sendUpgradeMessage() {
        ModNetwork.CHANNEL.sendToServer(new PowerLevelMessage(this.skillGroup.getId(), true));
    }

}
