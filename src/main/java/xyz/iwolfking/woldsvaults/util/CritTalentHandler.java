package xyz.iwolfking.woldsvaults.util;


import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.abilities.CritHitChanceTalent;
import xyz.iwolfking.woldsvaults.abilities.CritHitDamageTalent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public class CritTalentHandler {

    private static final Random RNG = new Random();
    private static final String FORCED_CRIT_TAG = "forced_crit";
    public static final String FULLY_CHARGED_TAG = "fully_charged";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void criticalHitChance(CriticalHitEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer sp)) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;

        boolean fullyCharged = sp.getPersistentData().getBoolean(FULLY_CHARGED_TAG);

        float critHitChance = getCritChance(sp);
        boolean isVanillaCrit = event.isVanillaCritical();
        sp.getPersistentData().putBoolean(FORCED_CRIT_TAG, false);

        if (isVanillaCrit) return;

        if (fullyCharged && critHitChance > 0f && roll(critHitChance)) {
            sp.getPersistentData().putBoolean(FORCED_CRIT_TAG, true);
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);

            if (sp.level instanceof ServerLevel sl) {
                LivingEntity target = (LivingEntity) event.getTarget();
                sl.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0D, target.getZ(),
                        12, 0.3D, 0.4D, 0.3D, 0.1D);
                sl.playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer sp)) return;

        float strength = sp.getAttackStrengthScale(0.0F); // server-side, use 0.0F
        boolean fullyCharged = strength >= 0.9F;          // pick threshold you want

        sp.getPersistentData().putBoolean(FULLY_CHARGED_TAG, fullyCharged);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void criticalHitDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer sp)) return;

        boolean forcedCrit = sp.getPersistentData().getBoolean(FORCED_CRIT_TAG);

        boolean vanillaCrit = sp.fallDistance > 0.0F
                && !sp.isOnGround()
                && !sp.onClimbable()
                && !sp.isInWater()
                && !sp.isPassenger()
                && sp.getEffect(MobEffects.BLINDNESS) == null;

        if (!forcedCrit && !vanillaCrit) return;

        float bonus = getCritDamage(sp);
        float mult = 1.0F;

        if (forcedCrit) {
            mult *= 1.5F;
        }
        mult *= (1.0F + bonus);

        event.setAmount(event.getAmount() * mult);

        if (forcedCrit) sp.getPersistentData().putBoolean(FORCED_CRIT_TAG, false);
    }



    private static float getCritChance(ServerPlayer player){
        TalentTree talents = PlayerTalentsData.get((ServerLevel) player.getLevel()).getTalents(player);
        float chance = 0.0f;
        for (CritHitChanceTalent critHitChanceTalent: talents.getAll(CritHitChanceTalent.class, Skill::isUnlocked)){
            chance = Math.max(chance, critHitChanceTalent.getPercentCritHitChance());
        }
        return chance;
    }
    private static float getCritDamage(ServerPlayer player){
        TalentTree talents = PlayerTalentsData.get((ServerLevel) player.getLevel()).getTalents(player);
        float damage = 0.0f;
        for (CritHitDamageTalent critHitDamageTalent: talents.getAll(CritHitDamageTalent.class, Skill::isUnlocked)){
            damage = Math.max(damage, critHitDamageTalent.getPercentCritHitDamage());
        }
        return damage;
    }
    private static boolean roll(float chance) {
        if (chance <= 0f) return false;
        if (chance >= 1f) return true;
        return RNG.nextFloat() < chance;
    }
}
