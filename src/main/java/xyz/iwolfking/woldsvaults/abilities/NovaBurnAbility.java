package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.NovaParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.EffectDurationHelper;
import iskallia.vault.util.damage.DamageOverTimeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import xyz.iwolfking.woldsvaults.events.WoldActiveFlags;
import xyz.iwolfking.woldsvaults.init.ModEffects;

import java.util.Optional;

public class NovaBurnAbility extends AbstractNovaAbility {
    private int durationSeconds;

    public NovaBurnAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float radius, float percentAbilityPowerDealt, float knockbackStrengthMultiplier, int durationSeconds) {
        super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, percentAbilityPowerDealt, knockbackStrengthMultiplier);
        this.durationSeconds = durationSeconds;
    }

    public NovaBurnAbility() {
    }

    public int getDurationSecondsUnmodified() {
        return this.durationSeconds;
    }

    public int getDurationTicks(LivingEntity entity) {
        int durationTicks = this.getDurationSecondsUnmodified() * 20;
        return EffectDurationHelper.adjustEffectDurationFloor(entity, (float)durationTicks);
    }

    @Override
    protected void burstAt(ServerPlayer player, Vec3 position, float damage, boolean isExtraCast) {
        int durationTicks = this.getDurationTicks(player);
        for (LivingEntity targetEntity : this.getTargetEntities(player.level, player, position)) {
            if (!isExtraCast) {
                DamageOverTimeHelper.invalidateAll(targetEntity);
            }
            ActiveFlags.IS_AP_ATTACKING.push();
            try {
                ActiveFlags.IS_EFFECT_ATTACKING.push();
                try {
                    DamageOverTimeHelper.applyDamageOverTime(targetEntity, DamageSource.playerAttack(player),
                            damage, durationTicks, WoldActiveFlags.IS_FIRE_NOVA_ATTACKING);
                } finally {
                    ActiveFlags.IS_EFFECT_ATTACKING.pop();
                }
            } finally {
                ActiveFlags.IS_AP_ATTACKING.pop();
            }
            targetEntity.addEffect(new MobEffectInstance(ModEffects.BURN, durationTicks, 0, false, true, false));
        }
    }
    protected void doParticles(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
            Vec3 pos = (Vec3)context.getSource().getPos().orElse(player.position());
            float radius = this.getRadius(player);
            ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new NovaParticleMessage(new Vec3(pos.x, pos.y + (double)0.15F, pos.z), radius));
        });
    }

    protected void doSound(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
            Vec3 pos = (Vec3)context.getSource().getPos().orElse(player.position());
            player.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.2F, 1.0F);
        });
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.durationSeconds, buffer);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.durationSeconds = (Integer)Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.INT.writeNbt(this.durationSeconds).ifPresent((tag) -> nbt.put("durationSeconds", tag));
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.durationSeconds = (Integer)Adapters.INT.readNbt(nbt.get("durationSeconds")).orElse(0);
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.INT.writeJson(this.durationSeconds).ifPresent((element) -> json.add("durationSeconds", element));
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.durationSeconds = (Integer)Adapters.INT.readJson(json.get("durationSeconds")).orElse(0);
    }

    public static class NovaDotEffect extends MobEffect {
        public NovaDotEffect(int color, ResourceLocation id) {
            super(MobEffectCategory.HARMFUL, color);
            this.setRegistryName(id);
        }
    }
}
