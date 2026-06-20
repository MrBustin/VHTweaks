package io.github.a1qs.vaultadditions.mixins.chain_thorns;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.PetEntity;
import iskallia.vault.skill.ability.effect.ShellQuillAbility;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.damage.ThornsReflectDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ShellQuillAbility.class, remap = false)
public abstract class MixinShellQuillAbility {
    @Unique private static Entity vaultAdditions$sourceEntity;

    @Inject(method = "thornsReflectDamage", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static void centerThornsAroundEntity(LivingAttackEvent event, CallbackInfo ci) {
        vaultAdditions$sourceEntity = event.getSource().getEntity();
    }

    @Redirect(method = "lambda$thornsReflectDamage$5", at = @At(value = "INVOKE", target = "Liskallia/vault/skill/ability/effect/ShellQuillAbility;doQuill(Lnet/minecraft/world/entity/player/Player;FI)V"))
    private static void centerThornsAroundEntity(Player player, float reflectedDamage, int quillCount, @Local(argsOnly = true) ShellQuillAbility ability) {
        if (vaultAdditions$sourceEntity instanceof LivingEntity source) {
            vaultAdditions$doQuill(player, source, ability, reflectedDamage);
        } else {
            doQuill(player, reflectedDamage, quillCount);
        }
        vaultAdditions$sourceEntity = null;
    }

    @Unique
    private static void vaultAdditions$doQuill(Player player, LivingEntity source, ShellQuillAbility ability, float reflectedDamage) {
        DamageSource src = ThornsReflectDamageSource.of(player);
        float chainRange = AreaOfEffectHelper.adjustAreaOfEffect(player, ability, 5.0F);
        List<Mob> nearby = EntityHelper.getNearby(player.getLevel(), source.blockPosition(), chainRange, Mob.class);
        nearby.removeIf(mob -> mob instanceof EternalEntity || mob instanceof PetEntity || mob.isInvulnerableTo(src));
        if (nearby.isEmpty()) {
            return;
        }

        nearby.sort(Comparator.comparing(e -> e.distanceTo(source)));
        nearby = nearby.subList(0, Math.min(ability.getQuillCount(), nearby.size()));
        float multiplier = 0.5F;

        for(Iterator<Mob> var6 = nearby.iterator(); var6.hasNext(); multiplier *= 0.5F) {
            Mob mob = var6.next();
            Vec3 movement = mob.getDeltaMovement();
            mob.hurt(src, reflectedDamage * multiplier);
            mob.setDeltaMovement(movement);
        }
    }

    @Shadow
    private static void doQuill(Player player, float reflectedDamage, int quillCount) {
        throw new IllegalStateException("Mixin failed to shadow doQuill method");
    }
}