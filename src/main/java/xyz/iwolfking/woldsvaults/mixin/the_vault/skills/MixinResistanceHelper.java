package xyz.iwolfking.woldsvaults.mixin.the_vault.skills;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import iskallia.vault.core.event.common.PlayerStatEvent;
import iskallia.vault.util.calc.PlayerStat;
import iskallia.vault.util.calc.ResistanceHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.iwolfking.woldsvaults.abilities.ColossusAbility;

import java.util.function.Consumer;

@Mixin(value = ResistanceHelper.class,remap = false)
public class MixinResistanceHelper {
    @WrapOperation(method = "getResistanceUnlimited",
            at = @At(value = "INVOKE", target = "Liskallia/vault/core/event/common/PlayerStatEvent;invoke(Liskallia/vault/util/calc/PlayerStat;Lnet/minecraft/world/entity/LivingEntity;FLjava/util/function/Consumer;)Liskallia/vault/core/event/common/PlayerStatEvent$Data;"))
    private static PlayerStatEvent.Data addColossusResistance(PlayerStatEvent instance, PlayerStat stat, LivingEntity entity, float value, Consumer<PlayerStatEvent.Data> config, Operation<PlayerStatEvent.Data> original) {
        return original.call(instance,stat,entity,value + ColossusAbility.ColossusEffect.getColossusResistance(entity),config);
    }
}
