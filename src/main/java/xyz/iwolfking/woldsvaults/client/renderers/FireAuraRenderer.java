package xyz.iwolfking.woldsvaults.client.renderers;

import iskallia.vault.client.data.ClientAbilityData;
import iskallia.vault.client.particles.EntityLockedParticle;
import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.abilities.EmpowerFireAuraAbility;
import xyz.iwolfking.woldsvaults.init.ModEffects;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class FireAuraRenderer {


    @SubscribeEvent
    public static void onTick(LivingEvent.LivingUpdateEvent event) {

        if ((event.getEntityLiving() instanceof Player player && player.level.isClientSide)
                && player.hasEffect(ModEffects.EMPOWER_FIRE_AURA)) {
            if(player != Minecraft.getInstance().player) return;
            float range = 1;
            for(EmpowerFireAuraAbility ability : ClientAbilityData.getTree().getAll(EmpowerFireAuraAbility.class, Ability::isUnlocked)) {
                range = ability.getRadius(player);
            }

            int color = 16738560;
            float red = ((color >>> 16) & 0xFF) / 255f;
            float green = ((color >>> 8) & 0xFF) / 255f;
            float blue = (color & 0xFF) / 255f;


            ParticleEngine pm = Minecraft.getInstance().particleEngine;

            for(int i = 0; i < 3; i++){
                float rotation = (float) Math.toRadians(player.tickCount % 90 * 4f + 120 * i);

                Vec3 offset = new Vec3(range * Math.cos(rotation), 0, range * Math.sin(rotation));

                Particle particle = pm.createParticle(ModParticles.ENTITY_LOCKED.get(), 0, 0, 0, range, (float) Math.toRadians(player.tickCount % 90 * 4f + 120 * i), 0);
                if (particle instanceof EntityLockedParticle entityLockedParticle) {
                    particle.setColor(Mth.clamp(red, 0, 1), Mth.clamp(green, 0, 1), Mth.clamp(blue, 0, 1));
                    entityLockedParticle.setEntity(player);
                    entityLockedParticle.setPosO();
                }
            }
        }

    }
}
