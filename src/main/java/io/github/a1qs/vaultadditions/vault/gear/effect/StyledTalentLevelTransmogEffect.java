package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.gear.attribute.VaultGearAttributeSerializer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.talent.TalentLevelAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class StyledTalentLevelTransmogEffect extends AttributeTransmogEffect<TalentLevelAttribute> {
    private static final int BERSERKING_COLOR = 0xD9A78D;
    private static final int LAST_STAND_COLOR = 0xB7613D;
    private static final int DEFAULT_COLOR = 0xD6C8B6;

    public static final StyledTalentLevelTransmogEffect TYPE = new StyledTalentLevelTransmogEffect(null);

    public StyledTalentLevelTransmogEffect(VaultGearAttributeInstance<TalentLevelAttribute> instance) {
        super(instance);
    }

    @Override
    public MutableComponent getTooltip() {
        VaultGearAttributeInstance<TalentLevelAttribute> instance = this.getVaultGearAttributeInstance();
        if (instance == null) {
            return null;
        }

        TalentLevelAttribute value = instance.getValue();
        int color = getDisplayColor(value.getTalent());
        Style style = Style.EMPTY.withColor(color);
        String talentName = ModConfigs.TALENTS.getTalentById(value.getTalent())
                .map(Skill::getName)
                .orElse(value.getTalent().replace('_', ' '));

        return new TextComponent("")
                .append(VaultGearModifier.AffixType.PREFIX.getAffixPrefixComponent(value.getLevelChange() >= 0).withStyle(style))
                .append(new TextComponent(String.valueOf(value.getLevelChange())).withStyle(style))
                .append(new TextComponent(" to level of ").withStyle(style))
                .append(new TextComponent(talentName).withStyle(style));
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        CompoundTag instance = new CompoundTag();
        instance.put("instance", VaultGearAttributeSerializer.serializeTag(this.getVaultGearAttributeInstance()));
        json.addProperty("instance", instance.toString());
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        try {
            CompoundTag instance = TagParser.parseTag(object.get("instance").getAsString());
            return new StyledTalentLevelTransmogEffect(VaultGearAttributeSerializer.deserializeTag(instance.getCompound("instance"), GearDataVersion.V0_7));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getDisplayColor(String talentId) {
        return switch (talentId) {
            case "Berserking" -> BERSERKING_COLOR;
            case "Last_Stand" -> LAST_STAND_COLOR;
            default -> DEFAULT_COLOR;
        };
    }
}
