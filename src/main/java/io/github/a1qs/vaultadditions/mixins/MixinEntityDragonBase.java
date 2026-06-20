package io.github.a1qs.vaultadditions.mixins;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafDragonAttacks;
import iskallia.vault.core.vault.modifier.spi.predicate.ModifierPredicate;
import iskallia.vault.entity.entity.elite.EliteModifierImmunity;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityDragonBase.class, remap = false)
public abstract class MixinEntityDragonBase extends LivingEntity implements EliteModifierImmunity {
    @Shadow public IafDragonAttacks.Ground groundAttack;

    protected MixinEntityDragonBase(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "randomizeAttacks", at = @At("TAIL"))
    public void cancelShakePrey(CallbackInfo ci) {
        if (ServerVaults.get(this.level).isPresent() && this.groundAttack == IafDragonAttacks.Ground.SHAKE_PREY) {
            randomizeAttacks();
        }
    }

    @Shadow public abstract void randomizeAttacks();

    @Override
    public ModifierPredicate getImmunity() {
        return ModifierPredicate.TRUE; // Immune to all modifiers
    }
}
