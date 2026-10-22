package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.entity.entity.VaultStormArrow;
import iskallia.vault.skill.ability.effect.spi.AbstractStormArrowAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.calc.EffectDurationHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec2;
import xyz.iwolfking.woldsvaults.entities.msc.VaultMeteorArrow;

import java.util.Optional;

public class MeteorStormAbility extends AbstractStormArrowAbility {
    private int burnDuration;
    private int amplifier;



    public MeteorStormAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost, float percentAttackDamageDealtMin, float percentAttackDamageDealtMax, float radius, int duration, int intervalTicks, int burnDuration, int amplifier) {
        super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, percentAttackDamageDealtMin, percentAttackDamageDealtMax, radius, duration, intervalTicks);
        this.burnDuration = burnDuration;
        this.amplifier = amplifier;

    }

    public MeteorStormAbility() {

    }
    @Override
    protected ActionResult doAction(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            int duration = this.getDuration(player);

            VaultMeteorArrow arrow = new VaultMeteorArrow(player.level, player);
            boolean allowPvP = VaultUtils.getVault(player.level)
                    .map(VaultUtils::isPvPVault)
                    .orElse(false);
            arrow.setAllowPvP(allowPvP);
            Vec2 shootRotation = getShootRotation(player);
            arrow.shootFromRotation(player, shootRotation.x, shootRotation.y, 0.0F, 1, 0F);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            arrow.setMeteorArrowType(VaultMeteorArrow.MeteorType.BASE);
            arrow.setDuration(duration);
            arrow.setRadius(getRadius(player));
            arrow.setIntervalTicks(getIntervalTicks());
            arrow.setAbilityPowerPercentRange(getAbilityPowerPercentMin(), getAbilityPowerPercentMax());
            arrow.setBurnDuration(getBurnDuration(player));
            arrow.setAmplifier(getAmplifier());


            player.level.addFreshEntity(arrow);
            player.level.playSound((Player) null, player, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.25F);


            return ActionResult.successCooldownDelayed(duration);
        }).orElse(ActionResult.fail());
    }


    public int getBurnDuration(LivingEntity player) {
        return EffectDurationHelper.adjustEffectDurationFloor(player, burnDuration);
    }

    public int getUnmodifiedBurnDuration() {
        return burnDuration;
    }

    public int getAmplifier() {
        return amplifier;
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.INT.writeBits(this.burnDuration, buffer);
        Adapters.INT.writeBits(this.amplifier, buffer);

    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.burnDuration = Adapters.INT.readBits(buffer).orElseThrow();
        this.amplifier = Adapters.INT.readBits(buffer).orElseThrow();

    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.INT.writeNbt(this.burnDuration).ifPresent(tag -> nbt.put("burnDuration", tag));
            Adapters.INT.writeNbt(this.amplifier).ifPresent(tag -> nbt.put("amplifier", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.burnDuration = Adapters.INT.readNbt(nbt.get("burnDuration")).orElse(200);
        this.amplifier = Adapters.INT.readNbt(nbt.get("amplifier")).orElse(20);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.INT.writeJson(this.burnDuration).ifPresent(element -> json.add("burnDuration", element));
            Adapters.INT.writeJson(this.amplifier).ifPresent(element -> json.add("amplifier", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.burnDuration = Adapters.INT.readJson(json.get("burnDuration")).orElse(200);
        this.amplifier = Adapters.INT.readJson(json.get("amplifier")).orElse(20);
    }
}
