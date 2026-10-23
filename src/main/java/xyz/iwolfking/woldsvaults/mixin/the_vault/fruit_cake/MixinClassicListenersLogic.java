//
// Created by BONNe
// Copyright - 2024
//


package xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.world.storage.VirtualWorld;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.data.CakeWorldData;

/**
 * This mixin adds `Fruit` to cake enter and exit messages & hides modifiers if the cake vault has fruit mode.
 */
@Mixin(value = ClassicListenersLogic.class, remap = false)
public class MixinClassicListenersLogic {
    @Redirect(method = "printJoinMessage", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/player/ClassicListenersLogic;getVaultObjective(Liskallia/vault/core/vault/Vault;)Ljava/lang/String;"))
    private String replaceEnterCakeText(ClassicListenersLogic instance, Vault vault, @Local(argsOnly = true) VirtualWorld world) {
        return CakeWorldData.isFruitCake(world) ? "Fruit Cake" : instance.getVaultObjective(vault);
    }

    @Redirect(method = "lambda$initServer$2", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/player/ClassicListenersLogic;getVaultObjective(Liskallia/vault/core/vault/Vault;)Ljava/lang/String;"))
    private String replaceExitCakeText(ClassicListenersLogic instance, Vault vault, @Local(argsOnly = true) VirtualWorld world) {
        return CakeWorldData.isFruitCake(world) ? "Fruit Cake" : instance.getVaultObjective(vault);
    }

    @Redirect(method = "printJoinMessage", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectIterator;hasNext()Z", ordinal = 0))
    private boolean hasNext(ObjectIterator<Object2IntMap.Entry<VaultModifier<?>>> instance, VirtualWorld world, Vault vault) {
        if (!instance.hasNext()) {
            return false;
        } else if (!"cake".equals(vault.get(Vault.OBJECTIVES).get(Objectives.KEY)) || !CakeWorldData.isFruitCake(world)) {
            return instance.hasNext();
        }
        return false;
    }
}
