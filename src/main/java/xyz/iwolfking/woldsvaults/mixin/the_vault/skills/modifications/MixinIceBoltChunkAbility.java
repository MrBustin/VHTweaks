package xyz.iwolfking.woldsvaults.mixin.the_vault.skills.modifications;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.entity.entity.IceBoltEntity;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.IntRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.StonefallFrostParticleMessage;
import iskallia.vault.skill.ability.effect.IceBoltChunkAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractIceBoltAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.AABBHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.iwolfking.woldsvaults.modifiers.gear.special.GlacialBlastModification;

import java.util.List;
import java.util.UUID;

@Mixin(value = IceBoltChunkAbility.class, remap = false)
public abstract class MixinIceBoltChunkAbility extends AbstractIceBoltAbility {

    @Shadow public abstract float getRadius(Entity attacker);

    @Shadow public abstract int getDurationTicks(LivingEntity entity);

    @Shadow public abstract int getAmplifier();

    @Shadow private float glacialChance;

    /**
     * @author
     * @reason
     */
    @Overwrite @Override
    protected ActionResult doAction(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map((player) -> {
            IceBoltEntity arrow = new IceBoltEntity(player, IceBoltEntity.Model.CHUNK, (result) -> {
                float radius = this.getRadius(player);
                Vec3 pos = result.getLocation();
                ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new StonefallFrostParticleMessage(pos, this.getRadius(player)));
                player.level.playSound(null, pos.x, pos.y, pos.z, ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
                List<LivingEntity> nearbyEntities = player.level.getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat().selector((entity) -> {
                    return !(entity instanceof Player);
                }).range(1000.0), player, AABBHelper.create(pos, radius));

                int hypothermia = 1;
                for(ConfiguredModification<GlacialBlastModification, IntRangeConfig, IntValue> mod : SpecialAbilityModification.getModifications(player, GlacialBlastModification.class)) {
                    hypothermia = mod.value().getValue();
                }
                if(hypothermia < 1) {
                    hypothermia = 1;
                }

                for (LivingEntity nearbyEntity : nearbyEntities) {

                    nearbyEntity.addEffect(new MobEffectInstance(ModEffects.CHILLED, this.getDurationTicks(player), this.getAmplifier(), false, false, false));


                    CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(player, nearbyEntity));
                    if (player.level.random.nextFloat() <= (this.glacialChance * hypothermia)) {
                        nearbyEntity.removeEffect(ModEffects.GLACIAL_SHATTER);
                        nearbyEntity.addEffect(new MobEffectInstance(ModEffects.GLACIAL_SHATTER, this.getDurationTicks(player), this.getAmplifier()));
                        player.level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.25F, 0.65F);
                    }
                }

            });
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.getThrowPower(), 0.0F);
            player.level.addFreshEntity(arrow);
            player.level.playSound(null, player, ModSounds.ICE_BOLT_CHUNK, SoundSource.PLAYERS, 1.0F, player.level.random.nextFloat() * 0.3F + 0.9F);
            return ActionResult.successCooldownImmediate();
        }).orElse(ActionResult.fail());
    }

    private static void setAbilityData(LivingEntity livingEntity, int intervalTicks, UUID playerUUID) {
        CompoundTag abilityData = getAbilityData(livingEntity);
        abilityData.putInt("intervalTicks", intervalTicks);
        abilityData.putInt("remainingIntervalTicks", 0);
        abilityData.putUUID("playerUUID", playerUUID);
    }

    private static CompoundTag getAbilityData(LivingEntity livingEntity) {
        CompoundTag persistentData = livingEntity.getPersistentData();
        CompoundTag abilityData = persistentData.getCompound("the_vault:ability/Nova_Slow");
        persistentData.put("the_vault:ability/Nova_Slow", abilityData);
        return abilityData;
    }
}
