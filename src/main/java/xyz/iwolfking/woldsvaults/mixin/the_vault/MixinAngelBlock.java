package xyz.iwolfking.woldsvaults.mixin.the_vault;

import iskallia.vault.block.AngelBlock;
import iskallia.vault.world.data.ServerVaults;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.items.rings.AngelRingItem;

@Restriction(
        require = {
                @Condition(type = Condition.Type.MOD, value = "angelring")
        }
)
@Mixin(value = AngelBlock.class, remap = false)
public abstract class MixinAngelBlock {


    @Inject(method = "isPlayerInRange", at = @At("HEAD"), cancellable = true)
    public void isInRange(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (AngelRingItem.isRingInCurioSlot(player) && ServerVaults.get(player.getLevel()).isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}

