package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.block.PylonBlock;
import iskallia.vault.config.TemporalShardConfig;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.expertises.PylonPilfererExpertise;

@Mixin(value = PylonBlock.class, remap = false)
public class MixinPylonBlock {
    @Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Liskallia/vault/config/TemporalShardConfig;getDropChance()F", remap = false), remap = true)
    private float modifyChance(TemporalShardConfig instance, @Local(argsOnly = true) Player player, @Local(argsOnly = true) Level level) {
        return instance.getDropChance() + getPylonPilfererBonus(player);
    }

    @Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Liskallia/vault/config/TemporalShardConfig;getUberChance()F", remap = false), remap = true)
    private float modifyUberChance(TemporalShardConfig instance, @Local(argsOnly = true) Player player, @Local(argsOnly = true) Level level) {
        return instance.getUberChance() + getPylonPilfererBonus(player);
    }

    private float getPylonPilfererBonus(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return 0.0F;
        }

        ExpertiseTree tree = PlayerExpertisesData.get(serverPlayer.getLevel()).getExpertises(player);
        if (tree == null) {
            return 0.0F;
        }

        final float[] bonusChance = {0.0F};
        tree.iterate(TieredSkill.class, skill -> {
            if (skill.isUnlocked() && skill.getChild() instanceof PylonPilfererExpertise pylonPilfererExpertise) {
                bonusChance[0] += pylonPilfererExpertise.getChanceIncrease();
            }
        });

        return bonusChance[0];
    }
}
