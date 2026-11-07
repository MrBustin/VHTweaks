package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.expertises.SurpriseModifiersExpertise;
import xyz.iwolfking.woldsvaults.util.VaultModifierUtils;

@Mixin(value = Runner.class, remap = false)
public abstract class MixinRunner extends Listener {

    @Inject(method = "onJoin", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/player/Listener;onJoin(Liskallia/vault/core/world/storage/VirtualWorld;Liskallia/vault/core/vault/Vault;)V"))
    private void addRandomPositiveModifiers(VirtualWorld world, Vault vault, CallbackInfo ci) {
        if(this.getPlayer().isPresent()) {
            ServerPlayer player = this.getPlayer().get();
            ExpertiseTree expertiseTree = PlayerExpertisesData.get(player.server).getExpertises(player);
            int surpriseModifiersExpertiseModifiers = 0;
            float surpriseModifiersExpertiseChance = 0;

            for(SurpriseModifiersExpertise expertise : expertiseTree.getAll(SurpriseModifiersExpertise.class, Skill::isUnlocked)) {
                surpriseModifiersExpertiseModifiers = Math.max(surpriseModifiersExpertiseModifiers, expertise.getModifierAmount());
                surpriseModifiersExpertiseChance = Math.max(surpriseModifiersExpertiseChance, expertise.getChance());
            }

            if(surpriseModifiersExpertiseModifiers > 0) {
                if(world.getRandom().nextFloat() < surpriseModifiersExpertiseChance) {
                    if(VaultUtils.isSpecialVault(vault) || VaultUtils.isRawVault(vault) || VaultUtils.isTrialVault(vault)) {
                        return;
                    }
                    else {
                        for(int i = 0; i < surpriseModifiersExpertiseModifiers; i++ ){
                            VaultModifierUtils.addModifierFromPool(vault, VaultMod.id("catalyst_positive"));
                        }
                    }
                }
            }
        }
    }
}
