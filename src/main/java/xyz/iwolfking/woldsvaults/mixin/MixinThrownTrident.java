package xyz.iwolfking.woldsvaults.mixin;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.NovaAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractSmiteAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.util.calc.AbilityPowerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.items.gear.VaultTridentItem;

@Mixin(ThrownTrident.class)
public abstract class MixinThrownTrident extends AbstractArrow {
    private static final int SMITE_DELAY_TICKS = 3;

    @Shadow public int clientSideReturnTridentTickCount;
    @Shadow private ItemStack tridentItem;
    @Shadow private boolean dealtDamage;

    private VaultGearData data = null;
    private LivingEntity pendingSmiteTarget = null;
    private int smiteDelayCounter = 0;

    protected MixinThrownTrident(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tickVaultTrident(CallbackInfo ci) {
        if (this.tridentItem == null || !(this.tridentItem.getItem() instanceof VaultTridentItem)) {
            return;
        }

        if (data == null) {
            data = VaultGearData.read(this.tridentItem);
        }

        if (pendingSmiteTarget != null) {
            smiteDelayCounter++;
            if (smiteDelayCounter >= SMITE_DELAY_TICKS) {
                Entity owner = this.getOwner();
                if (owner instanceof ServerPlayer serverPlayer && pendingSmiteTarget.isAlive()) {
                    applySmiteDamage(serverPlayer, pendingSmiteTarget);
                }
                pendingSmiteTarget = null;
                smiteDelayCounter = 0;
            }
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();

        int i = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.TRIDENT_LOYALTY, VaultGearAttributeTypeMerger.intSum());
        if (entity != null && i > 0 && (this.dealtDamage || this.isNoPhysics()) ) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level.isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double)i, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double)i;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }

        super.tick();
        ci.cancel();
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true, remap = true)
    private void onHitEntityWithVaultTrident(EntityHitResult p_37573_, CallbackInfo ci) {
        if (!(this.tridentItem.getItem() instanceof VaultTridentItem)) {
            return;
        } else if(!(p_37573_.getEntity() instanceof LivingEntity)) {
            ci.cancel();
            return;
        }

        Entity entity = p_37573_.getEntity();
        Double f = data.get(ModGearAttributes.ATTACK_DAMAGE, VaultGearAttributeTypeMerger.doubleSum());
        boolean hasSmiteAttribute = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.DISCHARGE, VaultGearAttributeTypeMerger.anyTrue());
        Entity entity1 = this.getOwner();
        if(entity1 instanceof Player player) {
            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
            MobType type = ((LivingEntity) entity).getMobType();
            float increasedDamage = 0.0F;
            if (!ActiveFlags.IS_AP_ATTACKING.isSet())
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_INCREASE, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            if (type == MobType.UNDEAD) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_UNDEAD, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (type == MobType.ARTHROPOD) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_SPIDERS, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (type == MobType.ILLAGER) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_ILLAGERS, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/nether"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_NETHER, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/champion"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_CHAMPION, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/dungeon"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_DUNGEON, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/tank"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_TANK, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/horde"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_HORDE, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/assassin"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_ASSASSIN, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }
            if (ModConfigs.ENTITY_GROUPS.isInGroup(VaultMod.id("mob_type/dweller"), (Entity)entity)) {
                increasedDamage += ((Float)snapshot.getAttributeValue(ModGearAttributes.DAMAGE_DWELLER, VaultGearAttributeTypeMerger.floatSum())).floatValue();
            }

            if (hasSmiteAttribute && entity1 instanceof ServerPlayer serverPlayer && entity instanceof LivingEntity livingTarget) {
                createSmiteVisualEffect(serverPlayer, livingTarget);
                pendingSmiteTarget = livingTarget;
                smiteDelayCounter = 0;
            }

            f += (f * (1.0F + increasedDamage));
        }

        DamageSource damagesource = DamageSource.trident(this, entity1 == null ? this : entity1);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f.floatValue())) {
            if (entity instanceof LivingEntity livingentity1) {
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        if (this.level instanceof ServerLevel && this.isVaultTridentChanneling()) {
            float channelChance = data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.CHANNELING_CHANCE, VaultGearAttributeTypeMerger.floatSum());
            if(channelChance <= level.random.nextFloat() || channelChance == 0.0) {
                ci.cancel();
                return;
            }
            BlockPos blockpos = entity.blockPosition();
            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level);
            if(lightningbolt != null) {
                lightningbolt.setDamage(f.floatValue());
            }
            lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
            lightningbolt.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer)entity1 : null);
            this.level.addFreshEntity(lightningbolt);
            soundevent = SoundEvents.TRIDENT_THUNDER;
            f1 = 5.0F;
            LivingEntity attacker = (LivingEntity) this.getOwner();

            AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(attacker);
            for (EffectCloudAttribute cloud : snapshot.getAttributeValue(ModGearAttributes.EFFECT_CLOUD, VaultGearAttributeTypeMerger.asList())) {
                MobEffect effect = cloud.getPrimaryEffect();
                if (effect == null) {
                    continue;
                }
                EffectCloudEntity cloudEntity = new EffectCloudEntity(attacker.getLevel(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
                cloud.apply(cloudEntity);
                cloudEntity.setOwner(attacker);
                attacker.getLevel().addFreshEntity(cloudEntity);
            }

            if (entity1 instanceof ServerPlayer player && hasSmiteAttribute) {
                Vec3 hitPos = entity.position();
                NovaAbility novaAbility = new NovaAbility(0, 0, 0, 0,0.0f,7.0f, 7.0f, 0.4f);
                SkillContext context = SkillContext.of(player);
                context.getSource().setPos(hitPos);
                novaAbility.onAction(context);
            }
        }

        this.playSound(soundevent, f1, 1.0F);
        ci.cancel();
    }

    private void createSmiteVisualEffect(ServerPlayer player, LivingEntity target) {
        final int BOLT_COLOR = 0x7777FF;
        AbstractSmiteAbility.SmiteBolt smiteBolt = ModEntities.SMITE_ABILITY_BOLT.create(player.level);
        if (smiteBolt != null) {
            smiteBolt.setColor(BOLT_COLOR);
            smiteBolt.moveTo(target.position());
            player.level.addFreshEntity(smiteBolt);
        }

        player.level.playSound((Player)null, target.getX(), target.getY(), target.getZ(),
                ModSounds.SMITE_BOLT, SoundSource.PLAYERS, 1.0F,
                1.0F + Mth.randomBetween(target.getRandom(), -0.2F, 0.2F));
    }

    private void applySmiteDamage(ServerPlayer player, LivingEntity target) {
        if (ActiveFlags.IS_AP_ATTACKING.isSet() || ActiveFlags.IS_SMITE_BASE_ATTACKING.isSet()) {
            return;
        }
        target.hurt(DamageSource.playerAttack(player), AbilityPowerHelper.getAbilityPower(player)* 3.0F);
    }

    private boolean isVaultTridentChanneling() {
        return data.get(xyz.iwolfking.woldsvaults.init.ModGearAttributes.TRIDENT_CHANNELING, VaultGearAttributeTypeMerger.anyTrue());
    }

    @Shadow protected abstract boolean isAcceptibleReturnOwner();
}