package xyz.iwolfking.woldsvaults.datagen.util;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.config.SkillDescriptionsConfig;
import iskallia.vault.config.TalentsConfig;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.FloatAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import xyz.iwolfking.vhapi.api.datagen.AbstractSkillDescriptionsProvider;
import xyz.iwolfking.vhapi.api.util.builder.description.JsonDescription;
import xyz.iwolfking.woldsvaults.api.util.ComponentUtils;

import java.util.Optional;

public class TalentDescriptionsHelper {

    public static void appendOverlevelDescription(String skillId, AbstractSkillDescriptionsProvider.Builder builder, String action, String overlevelBonusDesc, String descriptionColor) {
        if(ModConfigs.TALENTS == null) {
            ModConfigs.TALENTS = new TalentsConfig().readConfig();
        }

        if(ModConfigs.SKILL_DESCRIPTIONS == null) {
            ModConfigs.SKILL_DESCRIPTIONS = new SkillDescriptionsConfig().readConfig();
        }

        Optional<Skill> skill = Optional.empty();
        TalentTree tree = ModConfigs.TALENTS.tree;

        if(tree != null) {
            skill = tree.getForId(skillId);
        }

        String id = skill.map(Skill::getId).orElse(skillId);
        String name = skill.map(Skill::getName).orElse(skillId);

        builder.addDescription(id, jsonElements -> {
            if(ModConfigs.SKILL_DESCRIPTIONS != null && tree != null) {
                ComponentUtils.toJsonArray(ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(id)).forEach(jsonElements::add);
            }
            appendOverlevelDescription(name, action, overlevelBonusDesc, descriptionColor, jsonElements);
        });
    }

    public static void appendOverlevelDescription(String talentName, String action, String overlevelDescription, String descriptionColor, JsonArray jsonElements) {
        jsonElements.add(JsonDescription.text("\n\n"));
        jsonElements.add(JsonDescription.text("Overlevels of the "));
        jsonElements.add(JsonDescription.text(talentName, "gold"));
        jsonElements.add(JsonDescription.text(" talent "));
        jsonElements.add(JsonDescription.text(action + " "));
        jsonElements.add(JsonDescription.text(overlevelDescription, descriptionColor));
    }

    public enum NumericKind { INT, FLOAT, DOUBLE, OTHER }

    public static NumericKind detectNumericKind(VaultGearAttribute<?> attr) {

        if(attr.getRegistryName().equals(VaultMod.id("ability_power"))) {
            return NumericKind.INT;
        }

        if(attr.getGenerator() instanceof FloatAttributeGenerator) {
            return NumericKind.FLOAT;
        }
        else if(attr.getGenerator() instanceof IntegerAttributeGenerator) {
            return NumericKind.INT;
        }
        else if(attr.getGenerator() instanceof DoubleAttributeGenerator) {
            return NumericKind.DOUBLE;
        }

        return NumericKind.OTHER;
    }


}
