package xyz.iwolfking.woldsvaults.blocks.renderers.challenge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.init.ModRenderTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

public class TempleControllerRenderer implements BlockEntityRenderer<ChallengeControllerBlockEntity> {

    private final Font font;

    public TempleControllerRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(ChallengeControllerBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        double time = ClientScheduler.INSTANCE.getTick() + partialTick;

        if(entity.getState() == ChallengeControllerBlockEntity.State.IDLE) {
            double offsetTime = entity.animationTick;

            for(int i = 0; i < 3; i++) {
                offsetTime += 3 * 20;
                offsetTime *= 1.2;
                RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
                VertexConsumer buffer = bufferSource.getBuffer(renderType);
                poseStack.pushPose();
                poseStack.translate(0.5D, 1.65D, 0.5D);
                poseStack.scale( 7.0F, 7.0F, 7.0F);
                poseStack.mulPose(Quaternion.fromXYZ(0, (float)(offsetTime / 10.0F), 0));
                poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.D), (float)Math.sin(offsetTime / 40.D), (float)Math.sin(offsetTime / 20.0D)));
                this.renderCube(buffer, poseStack, (180 << 24) | 0x505050, combinedOverlay);
                poseStack.popPose();
            }
        } else if(entity.getState() == ChallengeControllerBlockEntity.State.GENERATING) {
            double offsetTime = entity.animationTick + partialTick;

            for(int i = 0; i < 3; i++) {
                offsetTime += 3 * 20;
                offsetTime *= 1.2;
                RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
                VertexConsumer buffer = bufferSource.getBuffer(renderType);
                double progress = Mth.clamp((entity.animationTick + partialTick) / 50, 0.0D, 1.0D);
                Color color = new Color(ColorUtil.blendColors(entity.getRenderer().getCoreColor(), 0x505050, (float)progress));
                poseStack.pushPose();
                poseStack.translate(0.5D, 1.65D + 0.5D * progress, 0.5D);
                poseStack.scale( 7.0F, 7.0F, 7.0F);
                poseStack.mulPose(Quaternion.fromXYZ(0, (float)(offsetTime / 10.0F), 0));
                poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.D), (float)Math.sin(offsetTime / 40.D), (float)Math.sin(offsetTime / 20.0D)));
                this.renderCube(buffer, poseStack, (180 << 24) | color.getRGB(), combinedOverlay);
                poseStack.popPose();
            }
        } else if(entity.getState() == ChallengeControllerBlockEntity.State.ACTIVE) {
            double offsetTime = entity.animationTick + partialTick;

            for(int i = 0; i < 3; i++) {
                offsetTime += 3 * 20;
                offsetTime *= 1.2;
                RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
                VertexConsumer buffer = bufferSource.getBuffer(renderType);
                Color color = new Color(entity.getRenderer().getCoreColor());
                poseStack.pushPose();
                poseStack.translate(0.5D, 2.15D, 0.5D);
                poseStack.scale( 7.0F, 7.0F, 7.0F);
                poseStack.mulPose(Quaternion.fromXYZ(0, (float)(offsetTime / 10.0F), 0));
                poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.D), (float)Math.sin(offsetTime / 40.D), (float)Math.sin(offsetTime / 20.0D)));
                this.renderCube(buffer, poseStack, (180 << 24) | color.getRGB(), combinedOverlay);
                poseStack.popPose();
            }
        }

        if(entity.getState() == ChallengeControllerBlockEntity.State.ACTIVE || entity.getState() == ChallengeControllerBlockEntity.State.GENERATING) {
            poseStack.pushPose();
            double progress = entity.animationTick == 0 ? 0.0D : Mth.clamp((entity.animationTick + partialTick) / 50, 0.0D, 1.0D);
            Color color = new Color(ColorUtil.blendColors(entity.getRenderer().getGlyphColor(), 0x505050, (float)progress));
            poseStack.translate(0.5D, 1.65D + 0.5D * progress, 0.5D);
            this.renderGlyphs(entity, poseStack, bufferSource, entity.animationTick == 0 ? 0.0D : entity.animationTick + partialTick, color.getRGB());
            poseStack.popPose();
        }
    }
    
    private void renderCube(VertexConsumer buffer, PoseStack poseStack, int color, int overlay) {
        float minU = 0;
        float minV = 0;
        float maxU = 1;
        float maxV = 1;

        this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, minU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F, -0.5F,  0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, -0.5F, -0.5F,  0.5F, color, minU, maxV, overlay);

        this.putVertex(poseStack, buffer, -0.5F,  0.5F,  0.5F, color, minU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F,  0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F, -0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, -0.5F,  0.5F, -0.5F, color, minU, maxV, overlay);

        this.putVertex(poseStack, buffer, -0.5F, -0.5F,  0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, -0.5F,  0.5F,  0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, -0.5F,  0.5F, -0.5F, color, minU, maxV, overlay);
        this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, minU, minV, overlay);

        this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F, -0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F,  0.5F, color, minU, maxV, overlay);
        this.putVertex(poseStack, buffer, 0.5F, -0.5F,  0.5F, color, minU, minV, overlay);

        this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, minU, minV, overlay);
        this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, -0.5F,  0.5F, -0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F, -0.5F, color, minU, maxV, overlay);

        this.putVertex(poseStack, buffer, -0.5F, -0.5F,  0.5F, color, minU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F, -0.5F,  0.5F, color, maxU, minV, overlay);
        this.putVertex(poseStack, buffer, 0.5F,  0.5F,  0.5F, color, maxU, maxV, overlay);
        this.putVertex(poseStack, buffer, -0.5F,  0.5F,  0.5F, color, minU, maxV, overlay);
    }

    public void putVertex(PoseStack matrices, VertexConsumer buffer, float x, float y, float z, int color, float u, float v, int overlay) {
        Matrix4f view = matrices.last().pose();
        Vector4f pos = new Vector4f(x / 16.0F, y / 16.0F, z / 16.0F, 1.0F);
        pos.transform(view);
        float alpha = (color >>> 24) / 255.0F;
        float red = (color >>> 16 & 0xFF) / 255.0F;
        float green = (color >>> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        buffer.vertex(pos.x(), pos.y(), pos.z(), red, green, blue, alpha, u, v, overlay,
                0, 0.0F, 0.0F, 0.0F);
    }

    private void renderGlyphs(ChallengeControllerBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, double time, int color) {
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.TOTEM_GLYPH_EFFECT);
        poseStack.pushPose();
        poseStack.mulPose(Quaternion.fromXYZ(0, (float)(time / 10.0F), 0));
        poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(time / 20.D), (float)Math.sin(time / 40.D), (float)Math.sin(time / 20.D)));
        int[] glyphIndices = this.getGlyphIndices(entity.getBlockPos());

        int alpha = color >>> 24;
        int red = color >>> 16 & 0xFF;
        int green = color >>> 8 & 0xFF;
        int blue = color & 0xFF;

        for(int i = 0; i < GLYPH_COUNT; i++) {
            float u0 = (glyphIndices[i] % GLYPH_TEXTURE_COLUMNS) * GLYPH_TEXTURE_GLYPH_U;
            float v0 = (glyphIndices[i] / GLYPH_TEXTURE_COLUMNS) * GLYPH_TEXTURE_GLYPH_V;
            float u1 = ((glyphIndices[i] % GLYPH_TEXTURE_COLUMNS) + 1) * GLYPH_TEXTURE_GLYPH_U;
            float v1 = ((glyphIndices[i] / GLYPH_TEXTURE_COLUMNS) + 1) * GLYPH_TEXTURE_GLYPH_V;

            buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0x0], GLYPH_RING_VERTICES[i * 3 * 4 + 0x1], GLYPH_RING_VERTICES[i * 3 * 4 + 0x2])
                    .color(red, green, blue, alpha)
                    .uv(u0, v0)
                    .uv2(0xF000F0)
                    .endVertex();
            buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0x3], GLYPH_RING_VERTICES[i * 3 * 4 + 0x4], GLYPH_RING_VERTICES[i * 3 * 4 + 0x5])
                    .color(red, green, blue, alpha)
                    .uv(u0, v1)
                    .uv2(0xF000F0)
                    .endVertex();
            buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0x6], GLYPH_RING_VERTICES[i * 3 * 4 + 0x7], GLYPH_RING_VERTICES[i * 3 * 4 + 0x8])
                    .color(red, green, blue, alpha)
                    .uv(u1, v1)
                    .uv2(0xF000F0)
                    .endVertex();
            buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0x9], GLYPH_RING_VERTICES[i * 3 * 4 + 0xA], GLYPH_RING_VERTICES[i * 3 * 4 + 0xB])
                    .color(red, green, blue, alpha)
                    .uv(u1, v0)
                    .uv2(0xF000F0)
                    .endVertex();
        }

        poseStack.popPose();
    }

    private int[] getGlyphIndices(BlockPos pos) {
        ChunkRandom random = ChunkRandom.any();
        random.setBlockSeed(0L, pos, 374761393L);
        int[] result = new int[GLYPH_COUNT];

        for (int i = 0; i < GLYPH_COUNT; i++) {
            result[i] = random.nextInt(GLYPH_INDEX_MIN, GLYPH_INDEX_MAX + 1);
        }

        return result;
    }

    private static final int GLYPH_COUNT = 16;
    private static final int GLYPH_ALPHA = (int)(0.80f * 255.0F);
    private static final float GLYPH_RING_RADIUS = 0.45F;
    private static final float GLYPH_QUAD_SIZE_X = 2 / 16f;
    private static final float GLYPH_QUAD_SIZE_Y = 2 / 16f;

    private static final int GLYPH_TEXTURE_GLYPH_WIDTH = 8;
    private static final int GLYPH_TEXTURE_GLYPH_HEIGHT = 8;
    private static final int GLYPH_TEXTURE_WIDTH = 128;
    private static final int GLYPH_TEXTURE_HEIGHT = 40;
    private static final int GLYPH_TEXTURE_COLUMNS = GLYPH_TEXTURE_WIDTH / GLYPH_TEXTURE_GLYPH_WIDTH;
    private static final float GLYPH_TEXTURE_GLYPH_U = GLYPH_TEXTURE_GLYPH_WIDTH / (float) GLYPH_TEXTURE_WIDTH;
    private static final float GLYPH_TEXTURE_GLYPH_V = GLYPH_TEXTURE_GLYPH_HEIGHT / (float) GLYPH_TEXTURE_HEIGHT;

    private static final int GLYPH_INDEX_MIN = 3;
    private static final int GLYPH_INDEX_MAX = 60;

    private static final float[] GLYPH_RING_VERTICES = new float[3 * 4 * GLYPH_COUNT];

    static {
        for (int i = 0; i < GLYPH_COUNT; i++) {
            double radians = 2 * Math.PI * (1.0 / GLYPH_COUNT) * i;
            float xCenter = (float) Math.cos(radians) * GLYPH_RING_RADIUS;
            float zCenter = (float) Math.sin(radians) * GLYPH_RING_RADIUS;

            Vec2 center = new Vec2(xCenter, zCenter);
            Vec2 perpendicular = new Vec2(zCenter, -xCenter).normalized().scale(GLYPH_QUAD_SIZE_X);

            Vec2 c0 = center.add(perpendicular);
            Vec2 c1 = center.add(perpendicular.negated());

            GLYPH_RING_VERTICES[i * 3 * 4 + 0x0] = c0.x;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x1] = GLYPH_QUAD_SIZE_Y;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x2] = c0.y;

            GLYPH_RING_VERTICES[i * 3 * 4 + 0x3] = c0.x;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x4] = -GLYPH_QUAD_SIZE_Y;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x5] = c0.y;

            GLYPH_RING_VERTICES[i * 3 * 4 + 0x6] = c1.x;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x7] = -GLYPH_QUAD_SIZE_Y;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0x8] = c1.y;

            GLYPH_RING_VERTICES[i * 3 * 4 + 0x9] = c1.x;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0xA] = GLYPH_QUAD_SIZE_Y;
            GLYPH_RING_VERTICES[i * 3 * 4 + 0xB] = c1.y;
        }
    }

}
