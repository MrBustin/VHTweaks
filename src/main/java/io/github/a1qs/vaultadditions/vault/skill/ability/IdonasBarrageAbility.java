package io.github.a1qs.vaultadditions.vault.skill.ability;

import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.entity.IdonasBarrageArrowEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.EffectDurationHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class IdonasBarrageAbility extends InstantManaAbility {
    private float damage;
    private float radius;
    private float duration;
    private float cooldown;
    private float damageFrequency;
    private float throwSpeed;

    public IdonasBarrageAbility(int unlockLevel, int learnPointCost, int regretCost, float manaCost, float damage, float radius, float duration, float cooldown, float damageFrequency, float throwSpeed) {
        super(unlockLevel, learnPointCost, regretCost, 0, manaCost);
        this.damage = damage;
        this.radius = radius;
        this.duration = duration;
        this.cooldown = cooldown;
        this.damageFrequency = damageFrequency;
        this.throwSpeed = throwSpeed;
    }

    public IdonasBarrageAbility() {
    }

    @Override
    public int getCooldownTicks() {
        return Math.max(0, Math.round(this.cooldown));
    }

    public float getDamage() {
        return this.damage;
    }

    public float getUnmodifiedRadius() {
        return this.radius;
    }

    public float getDuration() {
        return this.duration;
    }

    public float getConfiguredCooldown() {
        return this.cooldown;
    }

    public float getDamageFrequency() {
        return this.damageFrequency;
    }

    public int getDamageIntervalTicks() {
        return Math.max(1, Math.round(this.damageFrequency * 20.0F));
    }

    public float getThrowSpeed() {
        return this.throwSpeed;
    }

    public float getRadius(Entity attacker) {
        float realRadius = this.getUnmodifiedRadius();
        if (attacker instanceof LivingEntity livingEntity) {
            realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
        }

        return realRadius;
    }

    public int getDurationTicks(LivingEntity entity) {
        return EffectDurationHelper.adjustEffectDurationFloor(entity, this.duration);
    }

    @Override
    protected Ability.ActionResult doAction(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            IdonasBarrageArrowEntity arrow = new IdonasBarrageArrowEntity(player.level, player);
            arrow.configure(this.getRadius(player), this.getDurationTicks(player), this.damageFrequency, this.damage);
            arrow.setShotFromCrossbow(true);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, Math.max(0.1F, this.throwSpeed), 0.0F);
            player.level.addFreshEntity(arrow);
            player.level.playSound(null, player, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            return Ability.ActionResult.successCooldownImmediate();
        }).orElse(Ability.ActionResult.fail());
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.damage, buffer);
        Adapters.FLOAT.writeBits(this.radius, buffer);
        Adapters.FLOAT.writeBits(this.duration, buffer);
        Adapters.FLOAT.writeBits(this.cooldown, buffer);
        Adapters.FLOAT.writeBits(this.damageFrequency, buffer);
        Adapters.FLOAT.writeBits(this.throwSpeed, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.damage = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.duration = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.cooldown = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.damageFrequency = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.throwSpeed = Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.FLOAT.writeNbt(this.damage).ifPresent(tag -> nbt.put("damage", tag));
            Adapters.FLOAT.writeNbt(this.radius).ifPresent(tag -> nbt.put("radius", tag));
            Adapters.FLOAT.writeNbt(this.duration).ifPresent(tag -> nbt.put("duration", tag));
            Adapters.FLOAT.writeNbt(this.cooldown).ifPresent(tag -> nbt.put("barrageCooldown", tag));
            Adapters.FLOAT.writeNbt(this.damageFrequency).ifPresent(tag -> nbt.put("damageFrequency", tag));
            Adapters.FLOAT.writeNbt(this.throwSpeed).ifPresent(tag -> nbt.put("throwSpeed", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        CompoundTag baseNbt = nbt.copy();
        if (baseNbt.contains("cooldown") && !(baseNbt.get("cooldown") instanceof CompoundTag)) {
            baseNbt.remove("cooldown");
        }

        super.readNbt(baseNbt);
        this.damage = Adapters.FLOAT.readNbt(nbt.get("damage")).orElse(1.0F);
        this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(4.0F);
        this.duration = Adapters.FLOAT.readNbt(nbt.get("duration")).orElse(100.0F);
        this.cooldown = Adapters.FLOAT.readNbt(nbt.get("barrageCooldown"))
                .or(() -> Adapters.FLOAT.readNbt(nbt.get("cooldown")))
                .orElse(200.0F);
        this.damageFrequency = Adapters.FLOAT.readNbt(nbt.get("damageFrequency")).orElse(2.0F);
        this.throwSpeed = Adapters.FLOAT.readNbt(nbt.get("throwSpeed")).orElse(3.0F);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.FLOAT.writeJson(this.damage).ifPresent(element -> json.add("damage", element));
            Adapters.FLOAT.writeJson(this.radius).ifPresent(element -> json.add("radius", element));
            Adapters.FLOAT.writeJson(this.duration).ifPresent(element -> json.add("duration", element));
            Adapters.FLOAT.writeJson(this.cooldown).ifPresent(element -> json.add("barrageCooldown", element));
            Adapters.FLOAT.writeJson(this.damageFrequency).ifPresent(element -> json.add("damageFrequency", element));
            Adapters.FLOAT.writeJson(this.throwSpeed).ifPresent(element -> json.add("throwSpeed", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        JsonObject baseJson = json.deepCopy();
        if (baseJson.has("cooldown") && baseJson.get("cooldown").isJsonPrimitive()) {
            baseJson.remove("cooldown");
        }

        super.readJson(baseJson);
        this.damage = Adapters.FLOAT.readJson(json.get("damage")).orElse(1.0F);
        this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(4.0F);
        this.duration = Adapters.FLOAT.readJson(json.get("duration")).orElse(100.0F);
        this.cooldown = Adapters.FLOAT.readJson(json.get("barrageCooldown"))
                .or(() -> Adapters.FLOAT.readJson(json.get("cooldown")))
                .orElse(200.0F);
        this.damageFrequency = Adapters.FLOAT.readJson(json.get("damageFrequency")).orElse(2.0F);
        this.throwSpeed = Adapters.FLOAT.readJson(json.get("throwSpeed")).orElse(3.0F);
    }
}
