package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeSerializer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class StyledAbilityLevelTransmogEffect extends AttributeTransmogEffect<AbilityLevelAttribute> {
    private static final int DEFAULT_COLOR = 0xEE9D00;

    public static final StyledAbilityLevelTransmogEffect TYPE = new StyledAbilityLevelTransmogEffect(null, false);

    private final boolean allSpecializations;

    public StyledAbilityLevelTransmogEffect(VaultGearAttributeInstance<AbilityLevelAttribute> instance, boolean allSpecializations) {
        super(instance);
        this.allSpecializations = allSpecializations;
    }

    @Override
    public MutableComponent getTooltip() {
        VaultGearAttributeInstance<AbilityLevelAttribute> instance = this.getVaultGearAttributeInstance();
        if (instance == null) {
            return null;
        }

        AbilityLevelAttribute value = instance.getValue();
        Style style = Style.EMPTY.withColor(DEFAULT_COLOR);
        String abilityName = ModConfigs.ABILITIES.getAbilityById(value.getAbility())
                .map(Skill::getName)
                .orElse(value.getAbility().replace('_', ' '));

        MutableComponent tooltip = new TextComponent("")
                .append(VaultGearModifier.AffixType.PREFIX.getAffixPrefixComponent(value.getLevelChange() >= 0).withStyle(style))
                .append(new TextComponent(String.valueOf(value.getLevelChange())).withStyle(style))
                .append(new TextComponent(" to level of ").withStyle(style));

        if (this.allSpecializations) {
            tooltip.append(new TextComponent("all ").withStyle(style));
        }

        tooltip.append(new TextComponent(abilityName).withStyle(style));

        if (this.allSpecializations) {
            tooltip.append(new TextComponent(" skills").withStyle(style));
        }

        return tooltip;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        CompoundTag instance = new CompoundTag();
        instance.put("instance", VaultGearAttributeSerializer.serializeTag(this.getVaultGearAttributeInstance()));
        json.addProperty("instance", instance.toString());
        json.addProperty("allSpecializations", this.allSpecializations);
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        try {
            CompoundTag instance = TagParser.parseTag(object.get("instance").getAsString());
            boolean allSpecializations = object.has("allSpecializations") && object.get("allSpecializations").getAsBoolean();
            return new StyledAbilityLevelTransmogEffect(VaultGearAttributeSerializer.deserializeTag(instance.getCompound("instance"), GearDataVersion.V0_7), allSpecializations);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
