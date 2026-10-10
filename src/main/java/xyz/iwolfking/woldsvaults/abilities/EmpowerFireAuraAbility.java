package xyz.iwolfking.woldsvaults.abilities;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.EmpowerAbility;
import iskallia.vault.skill.ability.effect.spi.AbstractEmpowerAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.AABBHelper;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.effect.mobeffects.PercentBurnEffect;
import xyz.iwolfking.woldsvaults.init.ModEffects;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
@Mod.EventBusSubscriber(modid = "woldsvaults", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EmpowerFireAuraAbility extends AbstractEmpowerAbility {

    private static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER;
    private static final int FIRE_TICKS_DURATION = 40;

    private float radius;
    private int fireAmplifier;

    public EmpowerFireAuraAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks,
                                      float manaCostPerSecond, int durationTicks, float radius, int fireAmplifier) {
        super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCostPerSecond, durationTicks);
        this.radius = radius;
        this.fireAmplifier = fireAmplifier;
    }

    public EmpowerFireAuraAbility() {

    }

    public float getUnmodifiedRadius() {
        return this.radius;
    }

    public float getRadius(Entity attacker) {
        float realRadius = this.getUnmodifiedRadius();

        if (attacker instanceof LivingEntity livingEntity) {
            realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, this, realRadius);
        }

        return realRadius;
    }

    public int getFireAmplifier() {
        return this.fireAmplifier;
    }

    @Override
    protected ActionResult doToggle(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            if (this.isActive()) {
                int amplifier = Mth.clamp(this.getFireAmplifier() * 100, 0, 100);
                ModEffects.EMPOWER_FIRE_AURA.addTo(player, amplifier);
                return ActionResult.successCooldownDeferred();

            } else {
                player.removeEffect(ModEffects.EMPOWER_FIRE_AURA);
                return ActionResult.successCooldownImmediate();
            }
        }).orElse(ActionResult.fail());
    }

    @Override
    protected void doToggleSound(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(player -> {
            if (this.isActive()) {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.5f, 1f
                );
                player.playNotifySound(ModSounds.EMPOWER_ICE_ARMOUR, SoundSource.MASTER, 0.5f, 1f);
            }
        });
    }

    @Override
    public TickResult doInactiveTick(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            if (player.hasEffect(ModEffects.EMPOWER_FIRE_AURA)) {
                player.removeEffect(ModEffects.EMPOWER_FIRE_AURA);
            }

            return TickResult.PASS;
        }).orElse(TickResult.PASS);
    }

    @Override
    protected void doManaDepleted(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_FIRE_AURA));
    }

    @Override
    public void onRemove(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(entity -> entity.removeEffect(ModEffects.EMPOWER_FIRE_AURA));
    }

    @SubscribeEvent
    public static void on(LivingEvent.LivingUpdateEvent event) {

        if (!(event.getEntityLiving() instanceof ServerPlayer player)
                || !player.hasEffect(ModEffects.EMPOWER_FIRE_AURA)) {
            return;
        }

        AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

        for (EmpowerFireAuraAbility ability : abilities.getAll(EmpowerFireAuraAbility.class, Skill::isUnlocked)) {
            float radius = ability.getRadius(player);
            List<Mob> nearbyMobs = player.level.getNearbyEntities(
                    Mob.class, TargetingConditions.forCombat()
                            .range(radius)
                            .selector(MONSTER_PREDICATE),
                    player,
                    AABBHelper.create(player.position(), radius)
            );

            nearbyMobs.forEach(mob -> {
                        if (!mob.hasEffect(ModEffects.BURN) || mob.getEffect(ModEffects.BURN).getAmplifier() < ability.getFireAmplifier()) {
                            PercentBurnEffect.applyBurnEffect(mob, FIRE_TICKS_DURATION, ability.getFireAmplifier(), (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE), player
                            );
                        }
                    }
            );
        }
    }

//    private static void spawnAuraParticles(ServerPlayer player, float radius) {
//        if (!(player.level instanceof ServerLevel level) || level.getGameTime() % 4 != 0) {
//            return;
//        }
//
//        int particleCount = Mth.clamp(Mth.ceil(radius * 4.0F), 12, 32);
//        double rotation = level.getGameTime() * 0.08;
//
//        for (int i = 0; i < particleCount; i++) {
//            double angle = rotation + Math.PI * 2.0 * i / particleCount;
//            double x = player.getX() + Math.cos(angle) * radius;
//            double z = player.getZ() + Math.sin(angle) * radius;
//
//            level.sendParticles(ParticleTypes.FLAME, x, player.getY() + 0.15, z, 1, 0.0, 0.05, 0.0, 0.01);
//        }
//    }

    public static class EmpowerFireAuraEffect extends EmpowerAbility.EmpowerEffect {

        public EmpowerFireAuraEffect(int color, ResourceLocation resourceLocation) {

            super(EmpowerFireAuraAbility.EmpowerFireAuraEffect.class, color, resourceLocation);
        }
    }


//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(this.radius, buffer);
        Adapters.INT_SEGMENTED_3.writeBits(this.fireAmplifier, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.radius = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.fireAmplifier = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.FLOAT.writeNbt(this.radius).ifPresent(tag -> nbt.put("radius", tag));
            Adapters.INT.writeNbt(this.fireAmplifier).ifPresent(tag -> nbt.put("fireAmplifier", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.radius = Adapters.FLOAT.readNbt(nbt.get("radius")).orElse(0.0F);
        this.fireAmplifier = Adapters.INT.readNbt(nbt.get("fireAmplifier")).orElse(0);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.FLOAT.writeJson(this.radius).ifPresent(element -> json.add("radius", element));
            Adapters.INT.writeJson(this.fireAmplifier).ifPresent(element -> json.add("fireAmplifier", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.radius = Adapters.FLOAT.readJson(json.get("radius")).orElse(0.0F);
        this.fireAmplifier = Adapters.INT.readJson(json.get("fireAmplifier")).orElse(0);
    }
}
