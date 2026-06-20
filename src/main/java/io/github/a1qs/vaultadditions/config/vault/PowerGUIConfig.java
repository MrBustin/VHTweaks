package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.SkillStyle;

import java.util.HashMap;

public class PowerGUIConfig extends Config {
    @Expose
    private HashMap<String, SkillStyle> styles;

    public String getName() {
        return "vaultadditions_power_gui_styles";
    }

    public HashMap<String, SkillStyle> getStyles() {
        return this.styles;
    }

    protected void reset() {
        this.styles = new HashMap<>();
    }
}
