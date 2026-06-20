package io.github.a1qs.vaultadditions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.entity.IdonasBarrageEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class IdonasBarrageRenderer extends EntityRenderer<IdonasBarrageEntity> {
    private static final float FALL_HEIGHT = 18.0F;
    private static final float ARROW_MODEL_SCALE = 0.05625F;
    private static final float MIN_FALL_TICKS = 10.0F;
    private static final float MAX_FALL_TICKS = 24.0F;
    private static final float TRAJECTORY_LEAN_DEGREES = 22.5F;
    private static final float TARGET_MARKER_Y_OFFSET = 0.1F;
    private static final float TARGET_MARKER_THICKNESS = 0.2F;
    private static final int TARGET_MARKER_SEGMENTS = 96;

    public IdonasBarrageRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull IdonasBarrageEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        renderTargetMarker(entity, poseStack, buffer);

        int arrowCount = Math.max(12, Math.min(52, Math.round(entity.getRadius() * 7.0F)));
        float age = entity.tickCount + partialTicks;
        float shotDirectionX = entity.getShotDirectionX();
        float shotDirectionZ = entity.getShotDirectionZ();
        float shotDirectionLength = Mth.sqrt(shotDirectionX * shotDirectionX + shotDirectionZ * shotDirectionZ);
        if (shotDirectionLength < 1.0E-4F) {
            shotDirectionX = 0.0F;
            shotDirectionZ = 1.0F;
        } else {
            shotDirectionX /= shotDirectionLength;
            shotDirectionZ /= shotDirectionLength;
        }

        float trajectoryDistance = FALL_HEIGHT * 0.42F;
        float baseYaw = (float)Math.toDegrees(Math.atan2(-shotDirectionZ, shotDirectionX));

        for (int i = 0; i < arrowCount; i++) {
            float fallTicks = MIN_FALL_TICKS + randomUnit(entity.getId(), i, 11) * (MAX_FALL_TICKS - MIN_FALL_TICKS);
            float restTicks = 4.0F + randomUnit(entity.getId(), i, 13) * 14.0F;
            float startOffset = randomUnit(entity.getId(), i, 17) * 42.0F;
            float localAge = age - startOffset;
            if (localAge < 0.0F) {
                continue;
            }

            float cycleLength = fallTicks + restTicks;
            int cycle = Mth.floor(localAge / cycleLength);
            float cycleAge = localAge - cycle * cycleLength;
            if (cycleAge > fallTicks) {
                continue;
            }

            int cycleIndex = i + cycle * 131;
            float radiusSample = Mth.sqrt(randomUnit(entity.getId(), cycleIndex, 23)) * entity.getRadius();
            float angle = randomUnit(entity.getId(), cycleIndex, 29) * Mth.TWO_PI;
            float landingX = Mth.cos(angle) * radiusSample;
            float landingZ = Mth.sin(angle) * radiusSample;
            float progress = cycleAge / fallTicks;
            float travelRemaining = trajectoryDistance * (1.0F - progress);
            float x = landingX - shotDirectionX * travelRemaining;
            float z = landingZ - shotDirectionZ * travelRemaining;
            float y = FALL_HEIGHT * (1.0F - progress);
            float yaw = baseYaw + (randomUnit(entity.getId(), cycleIndex, 37) - 0.5F) * 10.0F;
            float lean = TRAJECTORY_LEAN_DEGREES + (randomUnit(entity.getId(), cycleIndex, 41) - 0.5F) * 5.0F;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F + lean));
            poseStack.scale(ARROW_MODEL_SCALE, ARROW_MODEL_SCALE, ARROW_MODEL_SCALE);
            renderArrowModel(poseStack, buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity))), packedLight);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderTargetMarker(IdonasBarrageEntity entity, PoseStack poseStack, MultiBufferSource buffer) {
        float radius = Math.max(TARGET_MARKER_THICKNESS, entity.getRadius());
        float halfThickness = TARGET_MARKER_THICKNESS * 0.5F;
        float innerRadius = Math.max(0.0F, radius - halfThickness);
        float outerRadius = radius + halfThickness;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = poseStack.last().pose();

        for (int i = 0; i < TARGET_MARKER_SEGMENTS; i++) {
            float angle = i * Mth.TWO_PI / TARGET_MARKER_SEGMENTS;
            float nextAngle = (i + 1) * Mth.TWO_PI / TARGET_MARKER_SEGMENTS;
            addMarkerQuad(vertexConsumer, matrix4f,
                    Mth.cos(angle) * outerRadius, TARGET_MARKER_Y_OFFSET, Mth.sin(angle) * outerRadius,
                    Mth.cos(angle) * innerRadius, TARGET_MARKER_Y_OFFSET, Mth.sin(angle) * innerRadius,
                    Mth.cos(nextAngle) * innerRadius, TARGET_MARKER_Y_OFFSET, Mth.sin(nextAngle) * innerRadius,
                    Mth.cos(nextAngle) * outerRadius, TARGET_MARKER_Y_OFFSET, Mth.sin(nextAngle) * outerRadius);
        }

        addMarkerLine(vertexConsumer, matrix4f, -radius, -radius, radius, radius, TARGET_MARKER_THICKNESS);
        addMarkerLine(vertexConsumer, matrix4f, -radius, radius, radius, -radius, TARGET_MARKER_THICKNESS);
    }

    private static void addMarkerLine(VertexConsumer vertexConsumer, Matrix4f matrix4f, float startX, float startZ, float endX, float endZ, float thickness) {
        float dx = endX - startX;
        float dz = endZ - startZ;
        float length = Mth.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4F) {
            return;
        }

        float offsetX = -dz / length * thickness * 0.5F;
        float offsetZ = dx / length * thickness * 0.5F;
        addMarkerQuad(vertexConsumer, matrix4f,
                startX + offsetX, TARGET_MARKER_Y_OFFSET, startZ + offsetZ,
                endX + offsetX, TARGET_MARKER_Y_OFFSET, endZ + offsetZ,
                endX - offsetX, TARGET_MARKER_Y_OFFSET, endZ - offsetZ,
                startX - offsetX, TARGET_MARKER_Y_OFFSET, startZ - offsetZ);
    }

    private static void addMarkerQuad(VertexConsumer vertexConsumer, Matrix4f matrix4f,
                                      float x1, float y1, float z1,
                                      float x2, float y2, float z2,
                                      float x3, float y3, float z3,
                                      float x4, float y4, float z4) {
        addMarkerVertex(vertexConsumer, matrix4f, x1, y1, z1);
        addMarkerVertex(vertexConsumer, matrix4f, x2, y2, z2);
        addMarkerVertex(vertexConsumer, matrix4f, x3, y3, z3);
        addMarkerVertex(vertexConsumer, matrix4f, x4, y4, z4);
    }

    private static void addMarkerVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, float x, float y, float z) {
        vertexConsumer.vertex(matrix4f, x, y, z)
                .color(255, 0, 0, 180)
                .endVertex();
    }

    private static float randomUnit(int seed, int index, int salt) {
        int value = seed * 73428767 ^ index * 9122719 ^ salt * 19349663;
        value ^= value << 13;
        value ^= value >>> 17;
        value ^= value << 5;
        return (value & 0x7fffffff) / (float)Integer.MAX_VALUE;
    }

    private static void renderArrowModel(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexConsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

        for (int i = 0; i < 4; i++) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            vertex(matrix4f, matrix3f, vertexConsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexConsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexConsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexConsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
        }
    }

    private static void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalY, int normalZ, int packedLight) {
        vertexConsumer.vertex(matrix4f, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(0)
                .uv2(packedLight)
                .normal(matrix3f, normalX, normalY, normalZ)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IdonasBarrageEntity entity) {
        return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
    }
}
