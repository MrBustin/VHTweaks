package xyz.iwolfking.woldsvaults.api.gear.modification;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearModificationConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.init.ModGearModifications;
import xyz.iwolfking.woldsvaults.mixin.the_vault.accessors.VaultGearModifierHelperAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WoldGearModifierHelper {
    public static GearModification.Result addUnusualModifier(ItemStack stack, long worldGameTime, Random random) {
        VaultGearData data = VaultGearData.read(stack);
        VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
        if (cfg == null) {
            VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
            return GearModification.Result.errorUnmodifiable();
        } else if (!data.isModifiable()) {
            return GearModification.Result.errorUnmodifiable();
        } else {
            int itemLevel = data.getItemLevel();
            int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
            int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
            prefixes -= data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
            suffixes -= data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
            boolean hasUnusual = false;
            for(VaultGearModifier<?> modifier : data.getModifiers(VaultGearModifier.AffixType.PREFIX)) {
                hasUnusual = modifier.getCategories().contains(VaultGearModifier.AffixCategory.valueOf("UNUSUAL"));
                if(hasUnusual) {
                    break;
                }
            }

            if(hasUnusual) {
                return GearModification.Result.makeActionError("full");
            }

            for(VaultGearModifier<?> modifier : data.getModifiers(VaultGearModifier.AffixType.SUFFIX)) {
                hasUnusual = modifier.getCategories().contains(VaultGearModifier.AffixCategory.valueOf("UNUSUAL"));
                if(hasUnusual) {
                    break;
                }
            }

            if(hasUnusual) {
                return GearModification.Result.makeActionError("full");
            }

            if (prefixes <= 0 && suffixes <= 0) {
                return GearModification.Result.makeActionError("full");
            } else {
                List<VaultGearModifier.AffixType> types = new ArrayList<>();
                if (prefixes > 0) {
                    types.add(VaultGearModifier.AffixType.PREFIX);
                }

                if (suffixes > 0) {
                    types.add(VaultGearModifier.AffixType.SUFFIX);
                }



                VaultGearModifier.AffixType type = MiscUtils.getRandomEntry(types, random);
                if(type == null) {
                    return GearModification.Result.errorInternal();
                }

                VaultGearTierConfig.ModifierAffixTagGroup modifierAffixTagGroup = VaultGearTierConfig.ModifierAffixTagGroup.valueOf("UNUSUAL_PREFIX");


                if(type.equals(VaultGearModifier.AffixType.PREFIX)) {
                    return cfg.getRandomModifier(modifierAffixTagGroup, itemLevel, random,data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS)).map(modifier -> {
                        data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
                        modifier.setGameTimeAdded(worldGameTime);
                        modifier.addCategory(VaultGearModifier.AffixCategory.valueOf("UNUSUAL"));
                        data.addModifier(type, modifier);
                        data.write(stack);
                        return GearModification.Result.makeSuccess();
                    }).orElse(GearModification.Result.makeActionError("no_modifiers"));
                }

                else if(type.equals(VaultGearModifier.AffixType.SUFFIX)) {
                    return cfg.getRandomModifier(VaultGearTierConfig.ModifierAffixTagGroup.valueOf("UNUSUAL_SUFFIX"), itemLevel, random, data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS)).map(modifier -> {
                        data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
                        modifier.setGameTimeAdded(worldGameTime);
                        modifier.addCategory(VaultGearModifier.AffixCategory.valueOf("UNUSUAL"));
                        data.addModifier(type, modifier);
                        data.write(stack);
                        return GearModification.Result.makeSuccess();
                    }).orElse(GearModification.Result.makeActionError("no_modifiers"));
                }

                return GearModification.Result.errorInternal();

            }
        }
    }

    public static GearModification.Result unfreezeAll(ItemStack stack) {
        VaultGearData data = VaultGearData.read(stack);
        List<VaultGearModifier<?>> affixes = new ArrayList<>();
        affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
        affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
        affixes.removeIf(modifier -> !modifier.hasCategory(VaultGearModifier.AffixCategory.FROZEN));
        if (affixes.isEmpty()) {
            return GearModification.Result.makeActionError("no_modifiers");
        } else {
            for (VaultGearModifier<?> mod : affixes) {
                mod.removeCategory(VaultGearModifier.AffixCategory.FROZEN);
            }
            data.write(stack);
            return GearModification.Result.makeSuccess();
        }
    }

    public static GearModification.Result canImproveRandomImplicit(ItemStack stack) {
        VaultGearData data = VaultGearData.read(stack);
        if (!data.isModifiable()) {
            return GearModification.Result.errorUnmodifiable();
        }

        return getImprovableImplicits(stack, data).isEmpty()
                ? GearModification.Result.makeActionError("all_max")
                : GearModification.Result.makeSuccess();
    }

    public static GearModification.Result improveRandomImplicit(ItemStack stack, long worldGameTime, Random random) {
        GearModification.Result canApply = canImproveRandomImplicit(stack);
        if (!canApply.success()) {
            return canApply;
        }

        VaultGearData data = VaultGearData.read(stack);
        Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>> candidate =
                MiscUtils.getRandomEntry(getImprovableImplicits(stack, data), random);
        if (candidate == null) {
            return GearModification.Result.errorInternal();
        }

        VaultGearTierConfig.ModifierOutcome<?> outcome = candidate.getB().getRandom(random).orElse(null);
        if (outcome == null) {
            return GearModification.Result.errorInternal();
        }

        VaultGearModifier modifier = candidate.getA();
        ConfigurableAttributeGenerator generator = modifier.getAttribute().getGenerator();
        Object maximumValue = generator.getMaximumValue(List.of(outcome.tier().getModifierConfiguration())).orElse(null);
        if (maximumValue == null) {
            return GearModification.Result.errorInternal();
        }

        VaultGearModificationConfig.OperationConfig operationConfig =
                ModConfigs.VAULT_GEAR_MODIFICATION_CONFIG.getOperationConfig(
                        ModGearModifications.EMPOWERED_FUNDAMENTAL_GEAR_MODIFICATION);
        float exponent = operationConfig == null ? 3.5F : operationConfig.getImprovementExponent();
        float minimumImprovement = operationConfig == null ? 0.5F : operationConfig.getMinImprovement();
        Object improvedValue = VaultGearModifierHelperAccessor.woldsvaults$calculateImprovedValue(
                modifier.getValue(), maximumValue, random, exponent, minimumImprovement);

        data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
        modifier.setValue(improvedValue);
        modifier.setRolledTier(outcome.tier().getModifierTier());
        modifier.setGameTimeAdded(worldGameTime);
        modifier.clearCategories();
        data.write(stack);
        return GearModification.Result.makeSuccess();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> getImprovableImplicits(
            ItemStack stack, VaultGearData data) {
        VaultGearTierConfig config = VaultGearTierConfig.getConfig(stack).orElse(null);
        if (config == null) {
            VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
            return List.of();
        }

        List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> candidates = new ArrayList<>();
        int itemLevel = data.getItemLevel();
        for (VaultGearModifier<?> modifier : data.getModifiers(VaultGearModifier.AffixType.IMPLICIT)) {
            if (!modifier.canBeModified()) {
                continue;
            }

            VaultGearTierConfig.ModifierTierGroup group = config.getTierGroup(modifier.getModifierIdentifier());
            VaultGearAttributeComparator comparator = modifier.getAttribute().getAttributeComparator();
            ConfigurableAttributeGenerator generator = modifier.getAttribute().getGenerator();
            if (group == null || comparator == null || generator == null) {
                continue;
            }

            WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
            for (VaultGearTierConfig.ModifierTier<?> tier : group.getModifiersForLevel(itemLevel)) {
                Object maximumValue = generator.getMaximumValue(List.of(tier.getModifierConfiguration())).orElse(null);
                if (maximumValue != null && comparator.compare(maximumValue, modifier.getValue()) > 0) {
                    outcomes.add(new VaultGearTierConfig.ModifierOutcome(tier, group), tier.getWeight());
                }
            }

            if (!outcomes.isEmpty()) {
                candidates.add(new Tuple<>(modifier, outcomes));
            }
        }
        return candidates;
    }
}
