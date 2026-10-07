package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Random;

@Mixin(value = VaultGearModifierHelper.class, remap = false)
public interface VaultGearModifierHelperAccessor {
    @Invoker("getAvailableModifierConfigurationOutcomes")
    static List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> woldsvaults$getAvailableModifierConfigurationOutcomes(
            VaultGearData data,
            ItemStack stack,
            boolean rerollTier
    ) {
        throw new AssertionError();
    }

    @Invoker("calculateImprovedValue")
    static Object woldsvaults$calculateImprovedValue(Object currentValue, Object maximumValue, Random random,
                                                      float improvementExponent, float minimumImprovement) {
        throw new AssertionError();
    }
}
