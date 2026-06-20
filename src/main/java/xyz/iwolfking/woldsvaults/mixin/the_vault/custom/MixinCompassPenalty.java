package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;


import iskallia.vault.network.message.ServerboundCompassModeSelectMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.expertises.CraftsmanExpertise;
import xyz.iwolfking.woldsvaults.expertises.EclecticGearExpertise;
import xyz.iwolfking.woldsvaults.expertises.NavigatorExpertise;

@Mixin(value = ServerboundCompassModeSelectMessage.class, remap = false)
public class MixinCompassPenalty {

    @Inject(
            method = "addCompassPenaltyModifier(Lnet/minecraft/server/level/ServerPlayer;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void noCompassPenalty(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if(player != null) {
            ExpertiseTree expertises = PlayerExpertisesData.get((ServerLevel) player.getLevel()).getExpertises(player);
            float chance = 0.0f;
            for (NavigatorExpertise navigatorExpertise: expertises.getAll(NavigatorExpertise.class, Skill::isUnlocked)) {
                chance = Math.max(chance, navigatorExpertise.getChanceIncrease());
            }
            if(player.getRandom().nextFloat() < chance) {
                cir.setReturnValue(true);
            }
        }
    }
}
