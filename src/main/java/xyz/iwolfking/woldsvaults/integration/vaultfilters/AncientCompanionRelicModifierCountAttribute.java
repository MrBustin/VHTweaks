package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.item.CompanionRelicItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.joseph.vaultfilters.attributes.abstracts.VaultAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AncientCompanionRelicModifierCountAttribute extends VaultAttribute<String> {
    private static final Map<Class<?>, BiFunction<String, Integer, ItemAttribute>> factories = new HashMap<>();

    private final int count;

    public AncientCompanionRelicModifierCountAttribute(String modifierId, Integer count) {
        super(modifierId);
        this.count = count == null || count < 1 ? 1 : count;
    }

    public void register(BiFunction<String, Integer, ItemAttribute> factory) {
        factories.put(this.getClass(), factory);
        super.register();
    }

    @Override
    public ItemAttribute withValue(String modifierId) {
        return withValue(modifierId, this.count);
    }

    public ItemAttribute withValue(String modifierId, Integer count) {
        return factories.getOrDefault(this.getClass(), (ignoredId, ignoredCount) -> null).apply(modifierId, count);
    }

    @Override
    public String getValue(ItemStack itemStack) {
        return null;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {
        return countModifiers(itemStack, this.value) >= this.count;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
        List<ItemAttribute> attributes = new ArrayList<>();

        getModifierCounts(itemStack).forEach((modifierId, modifierCount) -> {
            ItemAttribute attribute = withValue(modifierId, modifierCount);
            if (attribute != null) {
                attributes.add(attribute);
            }
        });

        return attributes;
    }

    private int countModifiers(ItemStack itemStack, String modifierId) {
        return getModifierCounts(itemStack).getOrDefault(modifierId, 0);
    }

    private Map<String, Integer> getModifierCounts(ItemStack itemStack) {
        Map<String, Integer> counts = new LinkedHashMap<>();

        if (!(itemStack.getItem() instanceof CompanionRelicItem) || !CompanionRelicItem.isAncient(itemStack)) {
            return counts;
        }

        for (ResourceLocation modifierId : CompanionRelicItem.getModifiers(itemStack)) {
            counts.merge(modifierId.toString(), 1, Integer::sum);
        }

        return counts;
    }

    @Override
    public void writeNBT(CompoundTag tag) {
        tag.putString(getNBTKey(), this.value);
        tag.putInt(getNBTKey() + "_count", this.count);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag tag) {
        String key = getNBTKey();
        int count = tag.contains(key + "_count") ? tag.getInt(key + "_count") : 1;
        return withValue(tag.getString(key), count);
    }

    @Override
    public String getNBTKey() {
        return "ancient_companion_relic_modifier_count";
    }

    @Override
    public String getTranslationKey() {
        return this.count == 1 ? getNBTKey() + "_single" : getNBTKey() + "_plural";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[] { getModifierDisplayName(this.value), this.count };
    }

    private String getModifierDisplayName(String modifierId) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(modifierId);
        if (resourceLocation == null) {
            return modifierId;
        }

        VaultModifier<?> modifier = VaultModifierRegistry.get(resourceLocation);
        return modifier == null ? modifierId : modifier.getDisplayName();
    }
}
