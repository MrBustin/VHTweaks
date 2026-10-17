package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.AbilitySuppressorTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.util.calc.EffectDurationHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import xyz.iwolfking.woldsvaults.entities.msc.ApexPredatorEntity;
import xyz.iwolfking.woldsvaults.init.ModEntities;

import java.util.Optional;

public class ApexPredatorAbility extends InstantManaAbility {
    private float radius;
    private float percentAttackDamageDealt;
    private double travelSpeed;
    private int stunDuration;
    private int maxRange;


    @Override
    protected ActionResult doAction(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            if (AbilitySuppressorTileEntity.hasAbilitySuppressorAround(player)) {
                player.displayClientMessage((new TextComponent("Ability Suppressor nearby")).withStyle(ChatFormatting.RED), true);
                return ActionResult.fail();
            } else {

                ServerLevel level = (ServerLevel) player.level;

                float pickupRadius = AreaOfEffectHelper.adjustAreaOfEffect(player, this, this.radius);
                Vec3 direction = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);

                if (direction.lengthSqr() < 1.0E-6D || travelSpeed <= 0.0D || maxRange <= 0) {
                    return ActionResult.fail();
                }

                direction = direction.normalize();

                float playerAttackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float finalDamage = (playerAttackDamage * this.percentAttackDamageDealt);

                int durationTicks = Math.max(1, (int) Math.ceil(maxRange / travelSpeed));
                ApexPredatorEntity predator = new ApexPredatorEntity(ModEntities.APEX_PREDATOR, level);
                double spawnDistance = player.getBbWidth() * 0.5D + predator.getBbWidth() * 0.5D + 0.25D;
                Vec3 spawnPosition = player.position().add(direction.scale(spawnDistance));

                predator.moveTo(spawnPosition.x, player.getY(), spawnPosition.z, player.getYRot(), 0.0F);
                predator.configure(
                        player,
                        direction,
                        travelSpeed,
                        finalDamage,
                        pickupRadius,
                        getStunDuration(player),
                        durationTicks
                );

                if (!level.addFreshEntity(predator)) {
                    return ActionResult.fail();
                }




                this.putOnCooldown(context);
                return ActionResult.successCooldownImmediate();
            }

        }).orElse(ActionResult.fail());
    }

    protected void doSound(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
            Vec3 pos = (Vec3)context.getSource().getPos().orElse(player.position());
            player.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 0.2F, 1.0F);
        });
    }

    // Getters
    public float getPercentAttackDamageDealt() {
        return percentAttackDamageDealt;
    }

    public float getRadius() {
        return radius;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public int getStunDuration(ServerPlayer player) {
        return EffectDurationHelper.adjustEffectDurationFloor(player, stunDuration);
    }

    public int getUnmodifiedStunDuration() {
        return stunDuration;
    }

    public double getTravelSpeed() {
        return travelSpeed;
    }

    //----------------------------------------------------------------------------------------------------------
    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.radius, buffer);
        Adapters.FLOAT.writeBits(this.percentAttackDamageDealt, buffer);
        Adapters.DOUBLE.writeBits(this.travelSpeed, buffer);
        Adapters.INT.writeBits(this.stunDuration, buffer);
        Adapters.INT.writeBits(this.maxRange, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.radius = Adapters.FLOAT.readBits(buffer).orElse(0.0F);
        this.percentAttackDamageDealt = Adapters.FLOAT.readBits(buffer).orElse(0.0F);
        this.travelSpeed = Adapters.DOUBLE.readBits(buffer).orElse(0.0);
        this.stunDuration = Adapters.INT.readBits(buffer).orElse(0);
        this.maxRange = Adapters.INT.readBits(buffer).orElse(0);
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.FLOAT.writeNbt(this.radius).ifPresent(tag -> nbt.put("radius", tag));
            Adapters.FLOAT.writeNbt(this.percentAttackDamageDealt).ifPresent(tag -> nbt.put("percentAttackDamageDealt", tag));
            Adapters.DOUBLE.writeNbt(this.travelSpeed).ifPresent(tag -> nbt.put("travelSpeed", tag));
            Adapters.INT.writeNbt(this.stunDuration).ifPresent(tag -> nbt.put("stunDuration", tag));
            Adapters.INT.writeNbt(this.maxRange).ifPresent(tag -> nbt.put("maxRange", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
        this.percentAttackDamageDealt = Adapters.FLOAT.readNbt(nbt.get("percentAttackDamageDealt")).orElse(0.0F);
        this.travelSpeed = Adapters.DOUBLE.readNbt(nbt.get("travelSpeed")).orElse(0.0);
        this.stunDuration = Adapters.INT.readNbt(nbt.get("stunDuration")).orElse(0);
        this.maxRange = Adapters.INT.readNbt(nbt.get("maxRange")).orElse(0);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.FLOAT.writeJson(this.radius).ifPresent(element -> json.add("radius", element));
            Adapters.FLOAT.writeJson(this.percentAttackDamageDealt).ifPresent(element -> json.add("percentAttackDamageDealt", element));
            Adapters.DOUBLE.writeJson(this.travelSpeed).ifPresent(element -> json.add("travelSpeed", element));
            Adapters.INT.writeJson(this.stunDuration).ifPresent(element -> json.add("stunDuration", element));
            Adapters.INT.writeJson(this.maxRange).ifPresent(element -> json.add("maxRange", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
        this.percentAttackDamageDealt = Adapters.FLOAT.readJson(json.get("percentAttackDamageDealt")).orElse(0.0F);
        this.travelSpeed = Adapters.DOUBLE.readJson(json.get("travelSpeed")).orElse(0.0);
        this.stunDuration = Adapters.INT.readJson(json.get("stunDuration")).orElse(0);
        this.maxRange = Adapters.INT.readJson(json.get("maxRange")).orElse(0);
    }
}
