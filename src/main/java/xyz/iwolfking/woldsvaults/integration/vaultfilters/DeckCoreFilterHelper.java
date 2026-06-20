package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.modifier.deck.BountyDeckModifier;
import iskallia.vault.core.card.modifier.deck.DeckModifier;
import iskallia.vault.core.card.modifier.deck.GlobalDeckModifier;
import iskallia.vault.core.card.modifier.deck.NonFoilEfficiencyDeckModifier;
import iskallia.vault.core.card.modifier.deck.ResourceDoubleDeckModifier;
import iskallia.vault.core.card.modifier.deck.ResourceRequirementDeckModifier;
import iskallia.vault.core.card.modifier.deck.SlotDeckModifier;
import iskallia.vault.core.card.modifier.deck.StatEfficiencyDeckModifier;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.DeckSocketItem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

final class DeckCoreFilterHelper {
    private static final String TAG_MODIFIER_MODEL = "ModifierModel";
    private static final String TAG_MODIFIER_POOL = "Modifier";
    private static final String TAG_MODIFIER_ROLL = "ModifierRoll";
    private static final String TAG_DECK_MODIFIER = "DeckModifier";
    private static final String TAG_SLOT_ROLL = "slotRoll";

    private DeckCoreFilterHelper() {
    }

    static Optional<String> getCoreId(ItemStack stack) {
        if (!isDeckCore(stack)) {
            return Optional.empty();
        }

        Optional<String> resolvedId = getResolvedModifier(stack)
                .map(DeckModifier::getId)
                .filter(id -> id != null && !id.isBlank())
                .map(DeckCoreFilterHelper::normalize);
        if (resolvedId.isPresent()) {
            return resolvedId;
        }

        Optional<String> rawModifier = getRawExactModifier(stack);
        if (rawModifier.isPresent()) {
            return rawModifier;
        }

        return getCoreName(stack).flatMap(DeckCoreFilterHelper::inferCoreId);
    }

    static Optional<String> getCoreTier(ItemStack stack) {
        if (!isDeckCore(stack)) {
            return Optional.empty();
        }

        Optional<String> rollId = getCoreRollId(stack);
        if (rollId.isPresent()) {
            return rollId;
        }

        if (getCoreId(stack).isPresent() || getTagString(stack, TAG_MODIFIER_MODEL).isPresent()) {
            return Optional.of("normal");
        }

        return Optional.empty();
    }

    static List<String> getCategories(ItemStack stack) {
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        Optional<String> rollId = getCoreRollId(stack);
        Optional<DeckModifier<?>> modifier = getResolvedModifier(stack);
        if (modifier.isPresent()) {
            addModifierCategories(modifier.get(), rollId.orElse(null), categories);
        }

        if (categories.isEmpty()) {
            getCoreId(stack).ifPresent(id -> addFallbackCategories(id, categories));
        }

        return new ArrayList<>(categories);
    }

    static List<String> getTargets(ItemStack stack) {
        LinkedHashSet<String> targets = new LinkedHashSet<>();
        Optional<String> rollId = getCoreRollId(stack);
        Optional<DeckModifier<?>> modifier = getResolvedModifier(stack);
        if (modifier.isPresent()) {
            addModifierTargets(modifier.get(), rollId.orElse(null), targets);
        }

        if (targets.isEmpty()) {
            getCoreId(stack).ifPresent(id -> addFallbackTargets(id, targets));
        }

        return new ArrayList<>(targets);
    }

    static Optional<Integer> getStrengthPercent(ItemStack stack) {
        return getResolvedModifier(stack)
                .map(DeckModifier::getModifierValue)
                .filter(value -> value >= 0.0f)
                .filter(value -> value != 0.0f || !(getResolvedModifier(stack).orElse(null) instanceof BountyDeckModifier))
                .map(value -> Math.round(value * 100.0f));
    }

    static Optional<Integer> getSlotCount(ItemStack stack) {
        if (!isDeckCore(stack)) {
            return Optional.empty();
        }

        Optional<Integer> storedSlotRoll = getStoredSlotRoll(stack);
        if (storedSlotRoll.isPresent()) {
            return storedSlotRoll;
        }

        Optional<DeckModifier<?>> modifier = getResolvedModifier(stack);
        if (modifier.isEmpty() || !(modifier.get() instanceof SlotDeckModifier slotModifier)) {
            return Optional.empty();
        }

        if (slotModifier.getAffectedSlots() != null && !slotModifier.getAffectedSlots().isEmpty()) {
            return Optional.of(slotModifier.getAffectedSlots().size());
        }

        IntRoll slotRoll = slotModifier.getConfig().getSlotRoll(getCoreRollId(stack).orElse(null));
        if (slotRoll != null && slotRoll.getMin() == slotRoll.getMax()) {
            return Optional.of(slotRoll.getMin());
        }

        return Optional.empty();
    }

    static String getCoreDisplayName(String coreId) {
        Optional<DeckModifier<?>> configModifier = getConfigModifier(coreId);
        if (configModifier.isPresent()) {
            String name = configModifier.get().getName();
            if (name != null && !name.isBlank()) {
                return name;
            }
        }

        return titleCase(coreId) + " Core";
    }

    static String displayValue(String value) {
        return titleCase(value);
    }

    private static boolean isDeckCore(ItemStack stack) {
        return stack != null && stack.getItem() instanceof DeckSocketItem;
    }

    private static Optional<DeckModifier<?>> getResolvedModifier(ItemStack stack) {
        if (!isDeckCore(stack)) {
            return Optional.empty();
        }

        try {
            Optional<DeckModifier<?>> modifier = DeckSocketItem.getDeckModifier(stack);
            if (modifier.isPresent()) {
                return modifier;
            }
        } catch (RuntimeException ignored) {
        }

        Optional<DeckModifier<?>> byModel = getTagString(stack, TAG_MODIFIER_MODEL).flatMap(DeckCoreFilterHelper::getModifierFromModel);
        if (byModel.isPresent()) {
            return byModel;
        }

        return getRawExactModifier(stack).flatMap(DeckCoreFilterHelper::getConfigModifier);
    }

    private static Optional<DeckModifier<?>> getModifierFromModel(String modelId) {
        if (!configsAvailable()) {
            return Optional.empty();
        }

        try {
            return ModConfigs.DECK_MODIFIERS.getFromModel(modelId).map(modifier -> (DeckModifier<?>) modifier);
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private static Optional<DeckModifier<?>> getConfigModifier(String coreId) {
        if (!configsAvailable() || coreId == null || coreId.isBlank()) {
            return Optional.empty();
        }

        try {
            return ModConfigs.DECK_MODIFIERS.getById(normalize(coreId));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    private static boolean configsAvailable() {
        try {
            return ModConfigs.isInitialized() && ModConfigs.DECK_MODIFIERS != null;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static Optional<String> getRawExactModifier(ItemStack stack) {
        return getTagString(stack, TAG_MODIFIER_POOL)
                .map(DeckCoreFilterHelper::normalize)
                .filter(value -> !value.startsWith("@"));
    }

    private static Optional<String> getCoreRollId(ItemStack stack) {
        Optional<String> selectedRoll = getResolvedModifier(stack)
                .map(modifier -> modifier.getConfig().getSelectedRollId())
                .filter(roll -> roll != null && !roll.isBlank())
                .map(DeckCoreFilterHelper::normalize);
        if (selectedRoll.isPresent()) {
            return selectedRoll;
        }

        return getTagString(stack, TAG_MODIFIER_ROLL)
                .map(DeckCoreFilterHelper::normalize)
                .or(() -> getCoreName(stack).flatMap(DeckCoreFilterHelper::inferCoreRollId));
    }

    private static Optional<String> getCoreName(ItemStack stack) {
        Optional<String> modifierName = getResolvedModifier(stack)
                .map(DeckModifier::getName)
                .filter(name -> name != null && !name.isBlank());
        if (modifierName.isPresent()) {
            return modifierName;
        }

        return getResolvedModifier(stack)
                .map(DeckModifier::getModelId)
                .filter(modelId -> modelId != null && !modelId.isBlank())
                .or(() -> getTagString(stack, TAG_MODIFIER_MODEL));
    }

    private static void addModifierCategories(DeckModifier<?> modifier, String rollId, Set<String> categories) {
        if (modifier instanceof GlobalDeckModifier globalModifier) {
            categories.add("Global");
            GlobalDeckModifier.Config config = globalModifier.getConfig();
            if (config.requiredColors != null && !config.requiredColors.isEmpty()) {
                categories.add("Color");
            }
            if (config.requiredGroups != null && !config.requiredGroups.isEmpty()) {
                categories.add("Group");
            }
            return;
        }

        if (modifier instanceof SlotDeckModifier slotModifier) {
            categories.add("Slot");
            SlotDeckModifier.Config config = slotModifier.getConfig();
            if (config.getRequiredColors(rollId) != null && !config.getRequiredColors(rollId).isEmpty()) {
                categories.add("Color");
            }
            if (config.getRequiredGroups(rollId) != null && !config.getRequiredGroups(rollId).isEmpty()) {
                categories.add("Group");
            }
            return;
        }

        if (modifier instanceof ResourceRequirementDeckModifier) {
            categories.add("Resource");
            categories.add("Requirement Reduction");
            return;
        }

        if (modifier instanceof ResourceDoubleDeckModifier) {
            categories.add("Resource");
            categories.add("Reward Doubling");
            return;
        }

        if (modifier instanceof BountyDeckModifier) {
            categories.add("Resource");
            categories.add("Bounty");
            return;
        }

        if (modifier instanceof StatEfficiencyDeckModifier) {
            categories.add("Stat Efficiency");
            categories.add("Scaling");
            return;
        }

        if (modifier instanceof NonFoilEfficiencyDeckModifier) {
            categories.add("Non-Foil Efficiency");
            categories.add("Scaling");
        }
    }

    private static void addModifierTargets(DeckModifier<?> modifier, String rollId, Set<String> targets) {
        if (modifier instanceof GlobalDeckModifier globalModifier) {
            GlobalDeckModifier.Config config = globalModifier.getConfig();
            addColors(config.requiredColors, targets);
            addGroups(config.requiredGroups, targets);
            return;
        }

        if (modifier instanceof SlotDeckModifier slotModifier) {
            SlotDeckModifier.Config config = slotModifier.getConfig();
            addColors(config.getRequiredColors(rollId), targets);
            addGroups(config.getRequiredGroups(rollId), targets);
            return;
        }

        if (modifier instanceof ResourceRequirementDeckModifier || modifier instanceof ResourceDoubleDeckModifier || modifier instanceof BountyDeckModifier) {
            targets.add("Resource");
            return;
        }

        if (modifier instanceof StatEfficiencyDeckModifier) {
            targets.add("Stat");
            return;
        }

        if (modifier instanceof NonFoilEfficiencyDeckModifier) {
            targets.add("Stat");
            targets.add("Evolution");
            targets.add("Non-Foil");
        }
    }

    private static void addFallbackCategories(String coreId, Set<String> categories) {
        switch (normalize(coreId)) {
            case "crimson", "golden", "azure", "viridian" -> {
                categories.add("Global");
                categories.add("Color");
            }
            case "shiny", "steadfast" -> {
                categories.add("Global");
                categories.add("Group");
            }
            case "empyreal" -> {
                categories.add("Slot");
                categories.add("Group");
            }
            case "harvest" -> {
                categories.add("Resource");
                categories.add("Requirement Reduction");
            }
            case "fortune" -> {
                categories.add("Resource");
                categories.add("Reward Doubling");
            }
            case "bounty" -> {
                categories.add("Resource");
                categories.add("Bounty");
            }
            case "equilibrium" -> {
                categories.add("Stat Efficiency");
                categories.add("Scaling");
            }
            case "pure" -> {
                categories.add("Non-Foil Efficiency");
                categories.add("Scaling");
            }
            default -> {
            }
        }
    }

    private static void addFallbackTargets(String coreId, Set<String> targets) {
        switch (normalize(coreId)) {
            case "crimson" -> targets.add("Red");
            case "golden" -> targets.add("Yellow");
            case "azure" -> targets.add("Blue");
            case "viridian" -> targets.add("Green");
            case "shiny" -> targets.add("Foil");
            case "steadfast", "equilibrium" -> targets.add("Stat");
            case "empyreal" -> {
                targets.add("Evolution");
                targets.add("Stat");
            }
            case "harvest", "fortune", "bounty" -> targets.add("Resource");
            case "pure" -> {
                targets.add("Stat");
                targets.add("Evolution");
                targets.add("Non-Foil");
            }
            default -> {
            }
        }
    }

    private static void addColors(Set<CardEntry.Color> colors, Set<String> targets) {
        if (colors == null) {
            return;
        }
        colors.stream().map(color -> titleCase(color.name())).forEach(targets::add);
    }

    private static void addGroups(Set<String> groups, Set<String> targets) {
        if (groups == null) {
            return;
        }
        groups.stream().filter(group -> group != null && !group.isBlank()).map(DeckCoreFilterHelper::titleCase).forEach(targets::add);
    }

    private static Optional<String> getTagString(ItemStack stack, String key) {
        if (!stack.hasTag()) {
            return Optional.empty();
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) {
            return Optional.empty();
        }
        String value = tag.getString(key);
        return value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    private static Optional<Integer> getStoredSlotRoll(ItemStack stack) {
        if (!stack.hasTag()) {
            return Optional.empty();
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_DECK_MODIFIER)) {
            return Optional.empty();
        }

        CompoundTag modifierTag = tag.getCompound(TAG_DECK_MODIFIER);
        return modifierTag.getTagType(TAG_SLOT_ROLL) == 3
                ? Optional.of(modifierTag.getInt(TAG_SLOT_ROLL))
                : Optional.empty();
    }

    private static Optional<String> inferCoreRollId(String value) {
        String normalized = normalize(value);
        if (normalized.contains("lesser")) {
            return Optional.of("lesser");
        }
        if (normalized.contains("greater")) {
            return Optional.of("greater");
        }
        return Optional.empty();
    }

    private static Optional<String> inferCoreId(String value) {
        String normalized = normalize(value);
        for (String id : List.of("crimson", "red", "golden", "yellow", "azure", "blue", "viridian", "green",
                "harvest", "pure", "equilibrium", "bounty", "steadfast", "empyreal", "fortune", "shiny")) {
            if (normalized.contains(id)) {
                return Optional.of(switch (id) {
                    case "red" -> "crimson";
                    case "yellow" -> "golden";
                    case "blue" -> "azure";
                    case "green" -> "viridian";
                    default -> id;
                });
            }
        }
        return Optional.empty();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String titleCase(String value) {
        String normalized = normalize(value).replace('_', ' ').replace('-', ' ');
        String[] parts = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.length() == 0 ? value : builder.toString();
    }
}
