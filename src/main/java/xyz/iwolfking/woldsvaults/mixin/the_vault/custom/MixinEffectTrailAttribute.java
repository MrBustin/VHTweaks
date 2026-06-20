package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import iskallia.vault.init.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

import java.util.Optional;

@Mixin(value = EffectTrialAttribute.class, remap = false)
public class MixinEffectTrailAttribute {

    @Inject(method = "playerTick", at = @At("HEAD"), cancellable = true)
    private static void poisonTrailOverride(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        Player player = event.player;

        if (player.level.isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer.isFallFlying()) return;
        if (!serverPlayer.isOnGround()) return;

        NonNullList<ItemStack> armorSlots = serverPlayer.getInventory().armor;

        int poisonDuration = 0;

        for (ItemStack stack : armorSlots) {
            if (!(stack.getItem() instanceof VaultGearItem)) continue;

            VaultGearData data = VaultGearData.read(stack);
            if (!data.hasAttribute(xyz.iwolfking.woldsvaults.init.ModGearAttributes.POISON_TRAIL)) continue;

            data.getFirstValue(xyz.iwolfking.woldsvaults.init.ModGearAttributes.POISON_TRAIL)
                    .ifPresent(attr -> {
                        // can't mutate poisonDuration in lambda; handled below
                    });

            // do it without lambda:
            var inst = data.getFirstValue(xyz.iwolfking.woldsvaults.init.ModGearAttributes.POISON_TRAIL);
            if (inst.isPresent()) {
                poisonDuration = Math.max(poisonDuration, inst.get().getDurationTicks());
            }
        }

        if (poisonDuration <= 0) return; // no poison trail -> let vanilla chilled run

        BlockPos pos = serverPlayer.blockPosition();
        EffectCloudEntity entity = new EffectCloudEntity(
                serverPlayer.level,
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D
        );

        entity.addEffect(new MobEffectInstance(ModEffects.POISON_OVERRIDE, poisonDuration, 0, true, true, true));
        entity.setRadius(0.75F);
        entity.setRadiusOnUse(-0.5F);
        entity.setDuration(poisonDuration);
        entity.setParticleData((ParticleOptions) ModParticles.CLOUD_EFFECT.get());
        entity.setOwner(serverPlayer);
        entity.setAffectsOwner(false);
        entity.setAffectsPlayers(false);

        serverPlayer.level.addFreshEntity(entity);

        ci.cancel(); // stop vanilla CHILLED trail
    }
    }


