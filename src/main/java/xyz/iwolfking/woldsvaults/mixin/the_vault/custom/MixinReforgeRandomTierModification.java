package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.operation.ReforgeRandomTierModification;
import iskallia.vault.util.MiscUtils;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.mixin.the_vault.accessors.VaultGearModifierHelperAccessor;

import java.util.List;
import java.util.Random;

@Mixin(value = ReforgeRandomTierModification.class, remap = false)
public class MixinReforgeRandomTierModification {
    @Inject(method = "doModification", at = @At("HEAD"), cancellable = true)
    private void avoidLegendaryModifiers(ItemStack stack, ItemStack materialStack, Player player, Random rand, CallbackInfoReturnable<GearModification.Result> cir) {
        cir.setReturnValue(reForgeOutcomeOfRandomNonLegendaryModifier(stack, player.getLevel().getGameTime(), rand));
    }

    private static GearModification.Result reForgeOutcomeOfRandomNonLegendaryModifier(ItemStack stack, long gameTime, Random rand) {
        VaultGearData data = VaultGearData.read(stack);
        if (!data.isModifiable()) {
            return GearModification.Result.errorUnmodifiable();
        }

        List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> outcomes =
                VaultGearModifierHelperAccessor.woldsvaults$getAvailableModifierConfigurationOutcomes(data, stack, true);

        outcomes.removeIf(outcome -> outcome.getB().size() <= 1
                || outcome.getA().hasCategory(VaultGearModifier.AffixCategory.LEGENDARY));

        if (outcomes.isEmpty()) {
            return GearModification.Result.makeActionError("no_modifiers");
        }

        Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>> selected =
                MiscUtils.getRandomEntry(outcomes);
        if (selected == null) {
            return GearModification.Result.errorInternal();
        }

        VaultGearTierConfig.ModifierOutcome<?> newOutcome = selected.getB().getRandom(rand).orElse(null);
        if (newOutcome == null) {
            return GearModification.Result.errorInternal();
        }

        VaultGearModifier<?> existingModifier = selected.getA();
        VaultGearModifier<?> newModifier = newOutcome.makeModifier(rand);
        VaultGearAttributeComparator comparator = existingModifier.getAttribute().getAttributeComparator();
        if (comparator != null && comparator.compare(existingModifier.getValue(), newModifier.getValue()) == 0) {
            return reForgeOutcomeOfRandomNonLegendaryModifier(stack, gameTime, rand);
        }

        data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
        setModifierValue(existingModifier, newModifier);
        existingModifier.setRolledTier(newModifier.getRolledTier());
        existingModifier.setGameTimeAdded(gameTime);
        existingModifier.clearCategories();
        data.write(stack);
        return GearModification.Result.makeSuccess();
    }

    @SuppressWarnings("unchecked")
    private static <T> void setModifierValue(VaultGearModifier<T> existingModifier, VaultGearModifier<?> newModifier) {
        existingModifier.setValue((T) newModifier.getValue());
    }
}
