package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModModels;
import io.github.a1qs.vaultadditions.init.ModSounds;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.util.SoundChoice;
import io.github.a1qs.vaultadditions.util.VaultGearAttributeHelper;
import io.github.a1qs.vaultadditions.vault.gear.effect.AbilitySoundTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.AttributeTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.ElytraSoundTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.HideElytraTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.StyledAbilityLevelTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.StyledTalentLevelTransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.TransmogEffect;
import io.github.a1qs.vaultadditions.vault.gear.effect.VanillaAttributeArmorTransmogEffect;
import iskallia.vault.config.Config;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.attribute.talent.TalentLevelAttribute;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransmogEffectsConfig extends Config {
    static {
        TransmogEffect.registerTypes();
    }

    @Expose
    private final JsonObject transmogEffects = new JsonObject();
    public final Map<DynamicModel<?>, List<TransmogEffect>> effects = new HashMap<>();

    public boolean hasEffect(Player player, TransmogEffect effect) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (hasEffect(player.getItemBySlot(slot), effect)) {
                return true;
            }
        }
        return hasEffect(ModelUtil.getWornSet(player), effect);
    }

    public boolean hasEffect(ItemStack itemStack, TransmogEffect effect) {
        return hasEffect(ModelUtil.getDynamicModel(itemStack, false), effect);
    }

    public boolean hasEffect(DynamicModel<?> model, TransmogEffect effect) {
        if (model == null) {
            return false;
        }
        List<TransmogEffect> effectList = effects.get(model);
        return effectList != null && effectList.contains(effect);
    }

    public <E extends TransmogEffect> E getEffect(Player player, Class<E> type) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            E effect = getEffect(player.getItemBySlot(slot), type);
            if (effect != null) {
                return effect;
            }
        }
        return getEffect(ModelUtil.getWornSet(player), type);
    }

    public <E extends TransmogEffect> E getEffect(ItemStack itemStack, Class<E> type) {
        return getEffect(ModelUtil.getDynamicModel(itemStack, false), type);
    }

    public <E extends TransmogEffect> E getEffect(DynamicModel<?> model, Class<E> type) {
        List<E> effects = getEffects(model, type);
        return effects.isEmpty() ? null : effects.get(0);
    }

    public <T extends TransmogEffect> List<T> getEffects(Player player, Class<T> type) {
        ArmorModel wornModel = ModelUtil.getWornSet(player);
        List<T> effects = new ArrayList<>(getEffects(wornModel, type));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            effects.addAll(getEffects(player.getItemBySlot(slot), type));
        }
        return effects;
    }

    public <E extends TransmogEffect> List<E> getEffects(ItemStack itemStack, Class<E> type) {
        return getEffects(ModelUtil.getDynamicModel(itemStack, false), type);
    }

    public <E extends TransmogEffect> List<E> getEffects(DynamicModel<?> model, Class<E> type) {
        if (model == null) {
            return new ArrayList<>();
        }
        List<TransmogEffect> effectList = effects.get(model);
        if (effectList == null) {
            return new ArrayList<>();
        }

        List<E> effects = new ArrayList<>();
        for (TransmogEffect effect : effectList) {
            if (type.isInstance(effect)) {
                effects.add(type.cast(effect));
            }
        }
        return effects;
    }

    public List<TransmogEffect> getEffects(Player player) {
        ArmorModel wornModel = ModelUtil.getWornSet(player);
        List<TransmogEffect> effects = new ArrayList<>(getEffects(wornModel));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            effects.addAll(getEffects(player.getItemBySlot(slot)));
        }
        return effects;
    }

    public List<TransmogEffect> getEffects(ItemStack itemStack) {
        return getEffects(ModelUtil.getDynamicModel(itemStack, false));
    }

    public List<TransmogEffect> getEffects(DynamicModel<?> model) {
        return model == null ? new ArrayList<>() : new ArrayList<>(effects.getOrDefault(model, List.of()));
    }

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        for (String key : transmogEffects.keySet()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            var model = ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(id).map(Pair::getFirst).orElse(null);
            if (id == null || model == null) {
                VaultAdditions.LOGGER.warn("Invalid transmog identifier: {}", key);
                continue;
            }

            List<TransmogEffect> effects = new ArrayList<>();
            JsonArray serializedEffects = transmogEffects.get(key) instanceof JsonArray array ? array : null;
            if (serializedEffects == null) {
                serializedEffects = new JsonArray();
                serializedEffects.add(transmogEffects.get(key));
            } else if (serializedEffects.isEmpty()) {
                VaultAdditions.LOGGER.info("No transmog effects for {}, skipping", key);
                continue;
            }

            for (JsonElement serializedEffect : serializedEffects) {
                TransmogEffect effect = TransmogEffect.deserializeEffect(serializedEffect);
                if (effect == null) {
                    VaultAdditions.LOGGER.warn("Invalid transmog effect {}/{}, skipping", key, serializedEffect);
                    continue;
                }
                effects.add(effect);
            }

            if (!effects.isEmpty()) {
                this.effects.put(model, Collections.unmodifiableList(effects));
            }
        }

        migrateAirMobilityBonuses(ModModels.HOKAGE_ARMOR, 0.2F, 0.1F, 0.05F, 0.05F);
        migrateAirMobilityBonuses(ModModels.HOY_ARMOR, 0.1F, 0.1F, 0.05F, 0.05F);
        migrateAirMobilityBonus(ModModels.Armor.CELESTIAL.getModel(), 0.1F, 0.1F, 0.05F, 0.1F);
        migrateHokageMovementSpeedBonuses(0.1F, 0.2F);
        migrateHoyManaCostBonuses(-0.25F, -0.35F);
        migrateKineticDamageReductionBonuses(ModModels.HOKAGE_ARMOR, 0.25F);
        migrateKineticDamageReductionBonuses(ModModels.HOY_ARMOR, 0.25F);
        migrateKineticDamageReductionBonus(ModModels.Armor.VIKING.getModel(), 0.25F);
        migrateKineticDamageReductionBonus(ModModels.Armor.CELESTIAL.getModel(), 0.25F);
        migrateFallDamageReductionBonuses(ModModels.HOKAGE_ARMOR, 0.25F);
        migrateFallDamageReductionBonuses(ModModels.HOY_ARMOR, 0.25F);
        migrateFallDamageReductionBonus(ModModels.Armor.VIKING.getModel(), 0.25F);
        migrateFallDamageReductionBonus(ModModels.Armor.CELESTIAL.getModel(), 0.25F);
        migrateFallDamageReductionBonus(ModModels.Armor.SPACE_MARINE.getModel(), 0.25F);
        migrateVikingBonuses();
        migrateSpaceMarineBonuses();
    }

    private void migrateAirMobilityBonuses(Iterable<ArmorModel> models, float legacyPairSpeed, float legacyControl, float playerBaseSpeed, float playerBaseAirMovement) {
        for (ArmorModel model : models) {
            migrateAirMobilityBonus(model, legacyPairSpeed, legacyControl, playerBaseSpeed, playerBaseAirMovement);
        }
    }

    private void migrateAirMobilityBonus(ArmorModel model, float legacyPairSpeed, float legacyControl, float playerBaseSpeed, float playerBaseAirMovement) {
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>();
        boolean hasSpeedBonus = false;
        boolean hasControlBonus = false;
        boolean changed = false;

        for (TransmogEffect effect : effects) {
            if (isAirMobilitySpeedBonus(effect, playerBaseSpeed)) {
                hasSpeedBonus = true;
            }

            if (isAirMobilityControlBonus(effect, playerBaseAirMovement)) {
                hasControlBonus = true;
            }

            if (isAirMobilityPairBonus(effect, legacyPairSpeed, legacyControl)
                    || isAirMobilityPairBonus(effect, playerBaseSpeed, playerBaseAirMovement)) {
                changed = true;
                continue;
            }

            if (legacyPairSpeed != playerBaseSpeed && isAirMobilitySpeedBonus(effect, legacyPairSpeed)) {
                changed = true;
                continue;
            }

            if (legacyControl != playerBaseAirMovement && isAirMobilityControlBonus(effect, legacyControl)) {
                changed = true;
                continue;
            }

            updatedEffects.add(effect);
        }

        if (!hasSpeedBonus) {
            updatedEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilitySpeed(playerBaseSpeed)));
            changed = true;
        }

        if (!hasControlBonus) {
            updatedEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilityControl(playerBaseAirMovement)));
            changed = true;
        }

        if (!changed) {
            return;
        }

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private void migrateHokageMovementSpeedBonuses(float legacyValue, float newValue) {
        for (ArmorModel model : ModModels.HOKAGE_ARMOR) {
            migrateVanillaAttributeBonus(model,
                    new VanillaAttributeArmorTransmogEffect<>(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, legacyValue),
                    new VanillaAttributeArmorTransmogEffect<>(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, newValue));
        }
    }

    private void migrateHoyManaCostBonuses(float legacyValue, float newValue) {
        for (ArmorModel model : ModModels.HOY_ARMOR) {
            migrateAttributeEffect(model,
                    new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage("Mana_Shield_Legacy", legacyValue)),
                    new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage("Mana_Shield_Legacy", newValue)));
            migrateAttributeEffect(model,
                    new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.SMITE_ARCHON, legacyValue)),
                    new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.SMITE_ARCHON, newValue)));
        }
    }

    private void migrateKineticDamageReductionBonuses(Iterable<ArmorModel> models, float value) {
        for (ArmorModel model : models) {
            migrateKineticDamageReductionBonus(model, value);
        }
    }

    private void migrateKineticDamageReductionBonus(ArmorModel model, float value) {
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>(effects);
        boolean hasKineticReduction = false;

        for (TransmogEffect effect : effects) {
            if (effect instanceof AttributeTransmogEffect<?> attributeEffect
                    && attributeEffect.getVaultGearAttributeInstance().getAttribute() == io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT
                    && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), value) == 0) {
                hasKineticReduction = true;
                break;
            }
        }

        if (hasKineticReduction) {
            return;
        }

        updatedEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, value)));

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private void migrateFallDamageReductionBonuses(Iterable<ArmorModel> models, float value) {
        for (ArmorModel model : models) {
            migrateFallDamageReductionBonus(model, value);
        }
    }

    private void migrateFallDamageReductionBonus(ArmorModel model, float value) {
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>(effects);
        boolean hasFallReduction = false;

        for (TransmogEffect effect : effects) {
            if (effect instanceof AttributeTransmogEffect<?> attributeEffect
                    && attributeEffect.getVaultGearAttributeInstance().getAttribute() == io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT
                    && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), value) == 0) {
                hasFallReduction = true;
                break;
            }
        }

        if (hasFallReduction) {
            return;
        }

        updatedEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, value)));

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private void migrateVikingBonuses() {
        ArmorModel model = ModModels.Armor.VIKING.getModel();
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>();
        boolean changed = false;
        boolean hasBerserkingBonus = false;
        boolean hasLastStandBonus = false;

        for (TransmogEffect effect : effects) {
            if (isLegacyVikingStrengthBonus(effect)) {
                changed = true;
                continue;
            }

            if (isTalentLevelBonus(effect, "Berserking", 2)) {
                hasBerserkingBonus = true;
                if (!(effect instanceof StyledTalentLevelTransmogEffect)) {
                    updatedEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Berserking", 2)));
                    changed = true;
                } else {
                    updatedEffects.add(effect);
                }
                continue;
            }

            if (isTalentLevelBonus(effect, "Last_Stand", 1)) {
                hasLastStandBonus = true;
                if (!(effect instanceof StyledTalentLevelTransmogEffect)) {
                    updatedEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Last_Stand", 1)));
                    changed = true;
                } else {
                    updatedEffects.add(effect);
                }
                continue;
            }

            updatedEffects.add(effect);
        }

        if (!hasBerserkingBonus) {
            updatedEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Berserking", 2)));
            changed = true;
        }

        if (!hasLastStandBonus) {
            updatedEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Last_Stand", 1)));
            changed = true;
        }

        if (!changed) {
            return;
        }

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private void migrateVanillaAttributeBonus(ArmorModel model, VanillaAttributeArmorTransmogEffect<?> legacyEffect, VanillaAttributeArmorTransmogEffect<?> newEffect) {
        migrateEffect(model, legacyEffect, newEffect);
    }

    private void migrateAttributeEffect(ArmorModel model, AttributeTransmogEffect<?> legacyEffect, AttributeTransmogEffect<?> newEffect) {
        migrateEffect(model, legacyEffect, newEffect);
    }

    private void migrateEffect(ArmorModel model, TransmogEffect legacyEffect, TransmogEffect newEffect) {
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>();
        boolean hasNewEffect = false;
        boolean changed = false;

        for (TransmogEffect effect : effects) {
            if (isSameEffect(effect, newEffect)) {
                hasNewEffect = true;
            }

            if (isSameEffect(effect, legacyEffect)) {
                changed = true;
                continue;
            }

            updatedEffects.add(effect);
        }

        if (!hasNewEffect) {
            updatedEffects.add(newEffect);
            changed = true;
        }

        if (!changed) {
            return;
        }

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private boolean isSameEffect(TransmogEffect effect, TransmogEffect expectedEffect) {
        return effect.serialize().equals(expectedEffect.serialize());
    }

    private boolean isAirMobilityPairBonus(TransmogEffect effect, float playerBaseSpeed, float playerBaseAirMovement) {
        if (!(effect instanceof AttributeTransmogEffect<?> attributeEffect)
                || attributeEffect.getVaultGearAttributeInstance().getAttribute() != io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.AIR_MOBILITY) {
            return false;
        }

        Object value = attributeEffect.getVaultGearAttributeInstance().getValue();
        if (!(value instanceof Pair<?, ?> pairValue)
                || !(pairValue.getFirst() instanceof Number speedValue)
                || !(pairValue.getSecond() instanceof Number airMovementValue)) {
            return false;
        }

        return Double.compare(speedValue.doubleValue(), playerBaseSpeed) == 0
                && Double.compare(airMovementValue.doubleValue(), playerBaseAirMovement) == 0;
    }

    private boolean isAirMobilitySpeedBonus(TransmogEffect effect, float playerBaseSpeed) {
        return effect instanceof AttributeTransmogEffect<?> attributeEffect
                && attributeEffect.getVaultGearAttributeInstance().getAttribute() == io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.AIR_MOBILITY_SPEED
                && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), playerBaseSpeed) == 0;
    }

    private boolean isAirMobilityControlBonus(TransmogEffect effect, float playerBaseAirMovement) {
        return effect instanceof AttributeTransmogEffect<?> attributeEffect
                && attributeEffect.getVaultGearAttributeInstance().getAttribute() == io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.AIR_MOBILITY_CONTROL
                && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), playerBaseAirMovement) == 0;
    }

    private boolean isLegacyVikingStrengthBonus(TransmogEffect effect) {
        if (!(effect instanceof AttributeTransmogEffect<?> attributeEffect)
                || attributeEffect.getVaultGearAttributeInstance().getAttribute() != ModGearAttributes.EFFECT) {
            return false;
        }

        Object value = attributeEffect.getVaultGearAttributeInstance().getValue();
        if (!(value instanceof EffectGearAttribute effectAttribute)) {
            return false;
        }

        return effectAttribute.getEffect() == MobEffects.DAMAGE_BOOST && effectAttribute.getAmplifier() == 10;
    }

    private boolean isTalentLevelBonus(TransmogEffect effect, String talentId, int levelChange) {
        if (!(effect instanceof AttributeTransmogEffect<?> attributeEffect)
                || attributeEffect.getVaultGearAttributeInstance().getAttribute() != ModGearAttributes.TALENT_LEVEL) {
            return false;
        }

        Object value = attributeEffect.getVaultGearAttributeInstance().getValue();
        if (!(value instanceof TalentLevelAttribute talentLevelAttribute)) {
            return false;
        }

        return talentId.equals(talentLevelAttribute.getTalent()) && talentLevelAttribute.getLevelChange() == levelChange;
    }

    private void migrateSpaceMarineBonuses() {
        ArmorModel model = ModModels.Armor.SPACE_MARINE.getModel();
        List<TransmogEffect> effects = new ArrayList<>(this.effects.getOrDefault(model, List.of()));
        List<TransmogEffect> updatedEffects = new ArrayList<>();
        boolean changed = false;
        boolean hasDashLevelBonus = false;
        boolean hasKineticReduction = false;

        for (TransmogEffect effect : effects) {
            if (isSpaceMarineStrengthBonus(effect) || isSpaceMarineAttackBonus(effect)) {
                changed = true;
                continue;
            }

            if (isAbilityLevelBonus(effect, ModAbilities.DASH, 1)) {
                hasDashLevelBonus = true;
                if (!(effect instanceof StyledAbilityLevelTransmogEffect)) {
                    updatedEffects.add(new StyledAbilityLevelTransmogEffect(VaultGearAttributeHelper.abilityLevel(ModAbilities.DASH, 1), true));
                    changed = true;
                } else {
                    updatedEffects.add(effect);
                }
                continue;
            }

            if (effect instanceof AttributeTransmogEffect<?> attributeEffect
                    && attributeEffect.getVaultGearAttributeInstance().getAttribute() == io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT
                    && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), 0.25F) == 0) {
                hasKineticReduction = true;
            }

            updatedEffects.add(effect);
        }

        if (!hasDashLevelBonus) {
            updatedEffects.add(new StyledAbilityLevelTransmogEffect(VaultGearAttributeHelper.abilityLevel(ModAbilities.DASH, 1), true));
            changed = true;
        }

        if (!hasKineticReduction) {
            updatedEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)));
            changed = true;
        }

        if (!changed) {
            return;
        }

        JsonArray serializedEffects = new JsonArray();
        for (TransmogEffect effect : updatedEffects) {
            serializedEffects.add(effect.serialize());
        }

        this.transmogEffects.add(model.getId().toString(), serializedEffects);
        this.effects.put(model, Collections.unmodifiableList(updatedEffects));
    }

    private boolean isSpaceMarineStrengthBonus(TransmogEffect effect) {
        if (!(effect instanceof AttributeTransmogEffect<?> attributeEffect)
                || attributeEffect.getVaultGearAttributeInstance().getAttribute() != ModGearAttributes.EFFECT) {
            return false;
        }

        Object value = attributeEffect.getVaultGearAttributeInstance().getValue();
        if (!(value instanceof EffectGearAttribute effectAttribute)) {
            return false;
        }

        return effectAttribute.getEffect() == MobEffects.DAMAGE_BOOST && effectAttribute.getAmplifier() == 10;
    }

    private boolean isSpaceMarineAttackBonus(TransmogEffect effect) {
        return effect instanceof AttributeTransmogEffect<?> attributeEffect
                && attributeEffect.getVaultGearAttributeInstance().getAttribute() == ModGearAttributes.DAMAGE_INCREASE
                && Double.compare(((Number) attributeEffect.getVaultGearAttributeInstance().getValue()).doubleValue(), 0.05F) == 0;
    }

    private boolean isAbilityLevelBonus(TransmogEffect effect, String abilityId, int levelChange) {
        if (!(effect instanceof AttributeTransmogEffect<?> attributeEffect)
                || attributeEffect.getVaultGearAttributeInstance().getAttribute() != ModGearAttributes.ABILITY_LEVEL) {
            return false;
        }

        Object value = attributeEffect.getVaultGearAttributeInstance().getValue();
        if (!(value instanceof AbilityLevelAttribute abilityLevelAttribute)) {
            return false;
        }

        return abilityId.equals(abilityLevelAttribute.getAbility()) && abilityLevelAttribute.getLevelChange() == levelChange;
    }

    @Override
    protected void reset() {
        for (ArmorModel model : ModModels.HOKAGE_ARMOR) {
            JsonArray effects = new JsonArray();
            effects.add(HideElytraTransmogEffect.TYPE.serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.EMPOWER, -0.25F)).serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilitySpeed(0.05F)).serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilityControl(0.05F)).serialize());
            effects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
            effects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
            effects.add(new VanillaAttributeArmorTransmogEffect<>(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, 0.2F).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.SMITE_ARCHON, new SoundChoice(ModSounds.TIGER_ACTIVATE_ARCHON.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect("Smite_Abstract", new SoundChoice(ModSounds.TIGER_ARCHON_BOLT.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.DASH, new SoundChoice(ModSounds.TIGER_DASH.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.MANA_SHIELD, new SoundChoice(ModSounds.TIGER_ACTIVATE_MANASHIELD.get())).serialize());
            transmogEffects.add(model.getId().toString(), effects);
        }

        for (ArmorModel model : ModModels.HOY_ARMOR) {
            JsonArray effects = new JsonArray();
            effects.add(HideElytraTransmogEffect.TYPE.serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage("Mana_Shield_Legacy", -0.35F)).serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.SMITE_ARCHON, -0.35F)).serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilitySpeed(0.05F)).serialize());
            effects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilityControl(0.05F)).serialize());
            effects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
            effects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
            effects.add(new ElytraSoundTransmogEffect(ModSounds.HOY_ELYTRA_GLIDE.get(), 0.2F).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.SMITE_ARCHON, new SoundChoice(ModSounds.HOY_ACTIVATE_ARCHON.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect("Smite_Abstract", new SoundChoice(ModSounds.HOY_ARCHON_BOLT.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.DASH, new SoundChoice(ModSounds.HOY_DASH.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.MANA_SHIELD, new SoundChoice(ModSounds.HOY_ACTIVATE_MANASHIELD.get())).serialize());
            effects.add(new AbilitySoundTransmogEffect(ModAbilities.MANA_SHIELD, new SoundChoice(ModSounds.HOY_MANASHIELD_HIT.get())).serialize());
            transmogEffects.add(model.getId().toString(), effects);
        }

        JsonArray vikingEffects = new JsonArray();
        vikingEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Berserking", 2)).serialize());
        vikingEffects.add(new StyledTalentLevelTransmogEffect(VaultGearAttributeHelper.talentLevel("Last_Stand", 1)).serialize());
        vikingEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityLevel("Rampage", 2)).serialize());
        vikingEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        vikingEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        transmogEffects.add(ModModels.Armor.VIKING.getModel().getId().toString(), vikingEffects);

        JsonArray celestialEffects = new JsonArray();
        celestialEffects.add(HideElytraTransmogEffect.TYPE.serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.EMPOWER, -0.25F)).serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilitySpeed(0.05F)).serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.airMobilityControl(0.1F)).serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        celestialEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityCooldownPercentage(ModAbilities.GHOST_WALK, -0.5F)).serialize());
        celestialEffects.add(new VanillaAttributeArmorTransmogEffect<>(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE, 0.1F).serialize());
        transmogEffects.add(ModModels.Armor.CELESTIAL.getModel().getId().toString(), celestialEffects);

        JsonArray spaceMarineEffects = new JsonArray();
        spaceMarineEffects.add(HideElytraTransmogEffect.TYPE.serialize());
        spaceMarineEffects.add(new AttributeTransmogEffect<>(VaultGearAttributeHelper.abilityManaCostPercentage(ModAbilities.DASH, -0.25F)).serialize());
        spaceMarineEffects.add(new StyledAbilityLevelTransmogEffect(VaultGearAttributeHelper.abilityLevel(ModAbilities.DASH, 1), true).serialize());
        spaceMarineEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(ModGearAttributes.RESISTANCE, 0.1F)).serialize());
        spaceMarineEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.KINETIC_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        spaceMarineEffects.add(new AttributeTransmogEffect<>(new VaultGearAttributeInstance<>(io.github.a1qs.vaultadditions.init.vault.ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, 0.25F)).serialize());
        spaceMarineEffects.add(new VanillaAttributeArmorTransmogEffect<>(ModAttributes.SIZE_SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL, 0.25F).serialize());
        transmogEffects.add(ModModels.Armor.SPACE_MARINE.getModel().getId().toString(), spaceMarineEffects);
    }

    @Override
    public String getName() {
        return "vaultadditions_transmog_effects";
    }
}
