package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.core.vault.challenge.curse.ChallengeCurseHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerChallengeManager;

import java.util.UUID;

/** Uses VH's native ability activation gate without changing other rooms' curses. */
@Mixin(value = ChallengeCurseHelper.class, remap = false)
public abstract class MixinChallengeCurseHelper {
    @Inject(method = "isAbilityDisabled", at = @At("RETURN"), cancellable = true)
    private static void woldsvaults$blockProtectVillagerAbilities(UUID playerId, String abilityId,
                                                               CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player != null && ProtectVillagerChallengeManager.isAbilityBlocked(player, abilityId)) {
            cir.setReturnValue(true);
        }
    }
}
