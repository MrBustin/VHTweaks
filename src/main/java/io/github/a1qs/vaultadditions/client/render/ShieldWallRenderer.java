package io.github.a1qs.vaultadditions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.client.particle.ShieldWallParticle;
import io.github.a1qs.vaultadditions.init.ModEffects;
import io.github.a1qs.vaultadditions.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ShieldWallRenderer {

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof Player player) {
            if (player.level.isClientSide && player.hasEffect(ModEffects.SHIELD_WALL)) {
                if (player != Minecraft.getInstance().player) {
                    return;
                }

                float range = 1.2F;

                int color = 7131872;
                float red = (float)(color >>> 16 & 255) / 255.0F;
                float green = (float)(color >>> 8 & 255) / 255.0F;
                float blue = (float)(color & 255) / 255.0F;
                ParticleEngine pm = Minecraft.getInstance().particleEngine;

                for(int i = 0; i < 3; ++i) {
                    float rotation = (float)Math.toRadians(((float)(player.tickCount % 90) * 4.0F + (float)(120 * i)));
                    new Vec3((double)range * Math.cos(rotation), 0.0F, (double)range * Math.sin(rotation));
                    Particle particle = pm.createParticle(ModParticles.SHIELD_WALL_PARTICLE.get(), 0.0F, 0.0F, 0.0F, range, (float)Math.toRadians((float)(player.tickCount % 90) * 4.0F + (float)(120 * i)), 0.0F);
                    if (particle instanceof ShieldWallParticle entityLockedParticle) {
                        particle.setColor(Mth.clamp(red, 0.0F, 1.0F), Mth.clamp(green, 0.0F, 1.0F), Mth.clamp(blue, 0.0F, 1.0F));
                        entityLockedParticle.setEntity(player);
                        entityLockedParticle.setPosO();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.hasEffect(ModEffects.SHIELD_WALL)) {
            renderShields(event.getPoseStack(), event.getMultiBufferSource(), player, event.getPartialTick(), 3, 0.8F);
        }
    }


    private static void renderShields(PoseStack stack, MultiBufferSource buffer, LivingEntity entity, float partialTicks, int count, float scale) {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(new ResourceLocation(VaultAdditions.MOD_ID, "shield"), "inventory");
        Direction[] directions = ArrayUtils.add(Direction.values(), null);

        float age = entity.tickCount + partialTicks;
        float rotateAngleY = age / -8.0F;
        float rotateAngleX = Mth.sin(age / 6.0F) / -4.0F;
        float rotateAngleZ = Mth.cos(age / 6.0F) / -4.0F;

        for (int c = 0; c < count; c++) {
            stack.pushPose();

            stack.translate(-0.5, 1.2, -0.5);

            stack.translate(0.5, -0.25, 0.5);
            stack.mulPose(Vector3f.ZP.rotationDegrees(rotateAngleZ * (90F / (float) Math.PI))); //LEFTRIGHT
            stack.mulPose(Vector3f.YP.rotationDegrees(rotateAngleY * (111F / (float) Math.PI) + (c * (360F / count)))); //MOVEMENT
            stack.mulPose(Vector3f.XP.rotationDegrees(rotateAngleX * (45F / (float) Math.PI))); // UPDOWN
            stack.translate(-0.5, -0.5, -0.5);

            // Push the shields further away from the centre
            stack.translate(0.0, 0.0, -1.0);
            stack.scale(scale, scale, scale);

            BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);

            for (Direction dir : directions) {
                Minecraft.getInstance().getItemRenderer().renderQuadList(
                        stack,
                        buffer.getBuffer(Sheets.translucentCullBlockSheet()),
                        model.getQuads(null, dir, entity.getRandom(), EmptyModelData.INSTANCE),
                        ItemStack.EMPTY,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY
                );
            }
            stack.popPose();
        }
    }
}
