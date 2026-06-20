package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.modification.operation.CorruptGearModification;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.api.util.PrestigePowerHelper;
import xyz.iwolfking.woldsvaults.prestige.GearSealerPrestigePower;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

@Mixin(value = CorruptGearModification.class, remap = false)
public class MixinCorruptGearModification {
    private static final int ADD_VORPAL_INDEX = 0;
    private static final int REPLACE_WITH_VORPAL_INDEX = 1;

    @Redirect(
            method = "doModification",
            at = @At(
                    value = "INVOKE",
                    target = "Liskallia/vault/core/util/WeightedList;getRandom(Ljava/util/random/RandomGenerator;)Ljava/util/Optional;"
            )
    )
    private Optional<Integer> woldsvaults$favorAddVorpal(WeightedList<Integer> weightedList, RandomGenerator randomGenerator, ItemStack stack, ItemStack materialStack, Player player, Random rand) {
        Optional<Integer> selected = weightedList.getRandom(randomGenerator);
        if (selected.isEmpty() || selected.get() != REPLACE_WITH_VORPAL_INDEX) {
            return selected;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return selected;
        }

        List<GearSealerPrestigePower> prestigePowers =
                PrestigePowerHelper.getPrestigePowersOfType(serverPlayer, GearSealerPrestigePower.class);
        if (prestigePowers.isEmpty()) {
            return selected;
        }

        float sealChance = 0.0f;
        for (GearSealerPrestigePower power : prestigePowers) {
            sealChance = Math.max(sealChance, power.getSealChance());
        }

        if (sealChance > 0.0f && rand.nextFloat() < sealChance) {
            return Optional.of(ADD_VORPAL_INDEX);
        }

        return selected;
    }
}
