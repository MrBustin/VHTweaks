package xyz.iwolfking.woldsvaults.gear.attribute.special;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.FloatRollRangeEntry;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.custom.loot.LootTriggerAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.mana.Mana;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class PercentManaPerLootAttribute extends LootTriggerAttribute {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
    private final float manaGenerationChance;
    private final float manaGenerated;

    protected PercentManaPerLootAttribute(ResourceLocation tileGroupId, String displayName, float manaGenerationChance, float manaGenerated) {
        super(tileGroupId, displayName);
        this.manaGenerationChance = manaGenerationChance;
        this.manaGenerated = manaGenerated;
    }

    public float getManaGenerationChance() {
        return this.manaGenerationChance;
    }

    public float getManaGenerated() {
        return this.manaGenerated;
    }

    @Override
    public void trigger(BlockEntity tile, RandomSource random, ServerPlayer player) {
        if (random.nextFloat() >= this.manaGenerationChance) {
            return;
        }

        float current = Mana.get(player);
        float total = Mana.getMax(player);
        Mana.set(player, ManaAction.PLAYER_ACTION, Math.min(total, this.manaGenerated * total + current));
    }

    public String toString() {
        String var10000 = String.valueOf(this.getTileGroupId());
        return "PercentManaPerLootAttribute{tileGroupId=" + var10000 + ", displayName='" + this.getDisplayName() + "', manaGenerationChance=" + this.manaGenerationChance + ", manaGenerated=" + this.manaGenerated + "}";
    }

    public static VaultGearAttributeType<PercentManaPerLootAttribute> type() {
        return VaultGearAttributeType.of((buf, attribute) -> {
            buf.writeIdentifier(attribute.getTileGroupId());
            buf.writeString(attribute.getDisplayName());
            buf.writeFloat(attribute.getManaGenerationChance());
            buf.writeFloat(attribute.getManaGenerated());

        }, (buf) -> new PercentManaPerLootAttribute(buf.readIdentifier(), buf.readString(), buf.readFloat(), buf.readFloat()), (buf, attribute) -> {
            NetcodeUtils.writeIdentifier(buf, attribute.getTileGroupId());
            NetcodeUtils.writeString(buf, attribute.getDisplayName());
            buf.writeFloat(attribute.getManaGenerationChance());
            buf.writeFloat(attribute.getManaGenerated());

        }, (buf) -> new PercentManaPerLootAttribute(NetcodeUtils.readIdentifier(buf), NetcodeUtils.readString(buf), buf.readFloat(), buf.readFloat()), VaultGearAttributeType.GSON::toJsonTree, (tag) -> {
            CompoundTag compoundTag = (CompoundTag)tag;
            ResourceLocation tileGroupId = ResourceLocation.parse(compoundTag.getString("tileGroupId"));
            String displayName = compoundTag.getString("displayName");
            float manaGenerationChance = compoundTag.getFloat("manaGenerationChance");
            float manaGenerated = compoundTag.getFloat("manaGenerated");
            return new PercentManaPerLootAttribute(tileGroupId, displayName, manaGenerationChance, manaGenerated);

        }, (attribute) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("tileGroupId", attribute.getTileGroupId().toString());
            compoundTag.putString("displayName", attribute.getDisplayName());
            compoundTag.putFloat("manaGenerationChance", attribute.getManaGenerationChance());
            compoundTag.putFloat("manaGenerated", attribute.getManaGenerated());
            return compoundTag;
        });
    }

    public static PercentManaPerLootAttribute.AttributeComparator comparator() {
        return new PercentManaPerLootAttribute.AttributeComparator();
    }

    public static PercentManaPerLootAttribute.Generator generator() {
        return new PercentManaPerLootAttribute.Generator();
    }

    public static PercentManaPerLootAttribute.Reader reader() {
        return new PercentManaPerLootAttribute.Reader();
    }

    private static class AttributeComparator extends VaultGearAttributeComparator<PercentManaPerLootAttribute> {
        private AttributeComparator() {
        }

        public Optional<PercentManaPerLootAttribute> merge(PercentManaPerLootAttribute thisValue, PercentManaPerLootAttribute thatValue) {
            if (!Objects.equals(thisValue.getTileGroupId(), thatValue.getTileGroupId())) {
                return Optional.empty();
            } else {
                return thisValue.getManaGenerated() != thatValue.getManaGenerated() ? Optional.empty() : Optional.of(new PercentManaPerLootAttribute(thisValue.getTileGroupId(), thisValue.getDisplayName(), thisValue.getManaGenerationChance() + thatValue.getManaGenerationChance(), thisValue.getManaGenerated()));
            }
        }

        public Optional<PercentManaPerLootAttribute> difference(PercentManaPerLootAttribute thisValue, PercentManaPerLootAttribute thatValue) {
            return Optional.empty();
        }

        @Nonnull
        public Comparator<PercentManaPerLootAttribute> getComparator() {
            return Comparator.comparing(PercentManaPerLootAttribute::getManaGenerated).thenComparing(PercentManaPerLootAttribute::getManaGenerationChance);
        }
    }

    private static class Reader extends VaultGearModifierReader<PercentManaPerLootAttribute> {
        protected Reader() {
            super("", 65535);
        }

        private Style getHighlightStyle() {
            return Style.EMPTY.withColor(20479);
        }

        @Nullable
        public MutableComponent getDisplay(VaultGearAttributeInstance<PercentManaPerLootAttribute> instance, VaultGearModifier.AffixType type) {
            PercentManaPerLootAttribute manaLootAttr = (PercentManaPerLootAttribute)instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(manaLootAttr);
            if (valueDisplay == null) {
                return null;
            } else {
                MutableComponent manaGenerated = getPercentageDisplay(manaLootAttr.getManaGenerated());
                return (new TextComponent(type.getAffixPrefix(manaLootAttr.getManaGenerationChance() >= 0.0F))).withStyle(this.getColoredTextStyle()).append(valueDisplay.withStyle(this.getHighlightStyle())).append((new TextComponent(" chance to generate ")).withStyle(this.getColoredTextStyle())).append(manaGenerated.withStyle(this.getHighlightStyle())).append((new TextComponent(" Mana per ")).withStyle(this.getColoredTextStyle())).append((new TextComponent(manaLootAttr.getDisplayName())).withStyle(this.getHighlightStyle())).append((new TextComponent(" looted")).withStyle(this.getColoredTextStyle()));
            }
        }

        @Nullable
        public MutableComponent getValueDisplay(PercentManaPerLootAttribute value) {
            DecimalFormat var10002 = PercentManaPerLootAttribute.FORMAT;
            float var10003 = value.getManaGenerationChance();
            return new TextComponent(var10002.format((double)(var10003 * 100.0F)) + "%");
        }

        protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<PercentManaPerLootAttribute> instance, VaultGearModifier.AffixType type) {
            PercentManaPerLootAttribute manaLootAttr = (PercentManaPerLootAttribute)instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(manaLootAttr);
            if (valueDisplay != null) {
                out.add(type.getAffixPrefix(manaLootAttr.getManaGenerationChance() >= 0.0F));
                out.add(valueDisplay.getString());
                out.add(" chance to generate ");
                out.add(getPercentageDisplay(manaLootAttr.getManaGenerated()).getString());
                out.add(" Mana per ");
                out.add(manaLootAttr.getDisplayName());
                out.add(" looted");
            }
        }
    }

    public static class Generator extends ConfigurableAttributeGenerator<PercentManaPerLootAttribute, PercentManaPerLootAttribute.Config> {
        public Generator() {
        }

        @Nullable
        public Class<PercentManaPerLootAttribute.Config> getConfigurationObjectClass() {
            return PercentManaPerLootAttribute.Config.class;
        }

        @Nullable
        public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<PercentManaPerLootAttribute> reader, PercentManaPerLootAttribute.Config min, PercentManaPerLootAttribute.Config max) {
            return this.getChanceDisplay(min.getManaGenerationChance().getMin()).append("-").append(this.getChanceDisplay(max.getManaGenerationChance().getMax())).append(", ").append(getPercentageDisplay(min.getManaGenerated().getMin())).append("-").append(getPercentageDisplay(max.getManaGenerated().getMax()));
        }

        private MutableComponent getChanceDisplay(float value) {
            return new TextComponent(PercentManaPerLootAttribute.FORMAT.format((double)(value * 100.0F)) + "%");
        }

        @Nullable
        public MutableComponent getConfigDisplay(VaultGearModifierReader<PercentManaPerLootAttribute> reader, PercentManaPerLootAttribute.Config object) {
            MutableComponent range = this.getConfigRangeDisplay(reader, object);
            MutableComponent display = new TextComponent(object.getDisplayName());
            return (new TextComponent("")).withStyle(reader.getColoredTextStyle()).append(range.withStyle(reader.getColoredTextStyle())).append(" Mana per ").append(display).append(" looted");
        }

        public PercentManaPerLootAttribute generateRandomValue(PercentManaPerLootAttribute.Config object, Random random) {
            JavaRandom rand = JavaRandom.ofScrambled(random.nextLong());
            float genChance = object.getManaGenerationChance().getRandom(rand);
            float genAmount = object.getManaGenerated().getRandom(rand);
            return new PercentManaPerLootAttribute(object.getTileEntityGroupId(), object.getDisplayName(), genChance, genAmount);
        }

        public Optional<PercentManaPerLootAttribute> getMinimumValue(List<PercentManaPerLootAttribute.Config> configurations) {
            Comparator<PercentManaPerLootAttribute.Config> cfgCmp = Comparator.comparing((config) -> config.getManaGenerated().getMin());
            cfgCmp = cfgCmp.thenComparing((config) -> config.getManaGenerationChance().getMin());
            return configurations.stream().min(cfgCmp).map((config) -> new PercentManaPerLootAttribute(config.getTileEntityGroupId(), config.getDisplayName(), config.getManaGenerationChance().getMin(), config.getManaGenerated().getMin()));
        }

        public Optional<PercentManaPerLootAttribute> getMaximumValue(List<PercentManaPerLootAttribute.Config> configurations) {
            Comparator<PercentManaPerLootAttribute.Config> cfgCmp = Comparator.comparing((config) -> config.getManaGenerated().getRolledMaximum());
            cfgCmp = cfgCmp.thenComparing((config) -> config.getManaGenerationChance().getRolledMaximum());
            return configurations.stream().max(cfgCmp).map((config) -> new PercentManaPerLootAttribute(config.getTileEntityGroupId(), config.getDisplayName(), config.getManaGenerationChance().getRolledMaximum(), config.getManaGenerated().getRolledMaximum()));
        }

        public Optional<Float> getRollPercentage(PercentManaPerLootAttribute value, List<PercentManaPerLootAttribute.Config> configurations) {
            return MiscUtils.getFloatValueRange(value.getManaGenerationChance(), this.getMinimumValue(configurations), this.getMaximumValue(configurations), PercentManaPerLootAttribute::getManaGenerationChance);
        }
    }

    public static class Config extends LootTriggerAttribute.Config {
        @Expose
        private final FloatRollRangeEntry manaGenerationChance;
        @Expose
        private final FloatRollRangeEntry manaGenerated;

        public Config(ResourceLocation tileEntityGroupId, String displayName, FloatRollRangeEntry manaGenerationChance, FloatRollRangeEntry manaGenerated) {
            super(tileEntityGroupId, displayName);
            this.manaGenerationChance = manaGenerationChance;
            this.manaGenerated = manaGenerated;
        }

        public FloatRollRangeEntry getManaGenerationChance() {
            return this.manaGenerationChance;
        }

        public FloatRollRangeEntry getManaGenerated() {
            return this.manaGenerated;
        }
    }

    private static MutableComponent getPercentageDisplay(float value) {
        return new TextComponent(FORMAT.format((double)(value * 100.0F)) + "%");
    }
}
