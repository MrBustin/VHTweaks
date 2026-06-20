package xyz.iwolfking.woldsvaults.mixin.create;

import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Arrays;

import static com.simibubi.create.foundation.block.render.SpriteShiftEntry.getUnInterpolatedU;
import static com.simibubi.create.foundation.block.render.SpriteShiftEntry.getUnInterpolatedV;

@Mixin(value = BakedModelHelper.class, remap = false)
public abstract class MixinBakedModelHelper {
    private static final double EPSILON = 1.0E-7;

    @Overwrite
    public static int[] cropAndMove(int[] vertexData, TextureAtlasSprite sprite, AABB crop, Vec3 move) {
        int[] copy = Arrays.copyOf(vertexData, vertexData.length);

        if (copy.length < 32 || sprite == null || !isFinite(crop) || !isFinite(move)) {
            return copy;
        }

        Vec3 xyz0 = BakedQuadHelper.getXYZ(copy, 0);
        Vec3 xyz1 = BakedQuadHelper.getXYZ(copy, 1);
        Vec3 xyz2 = BakedQuadHelper.getXYZ(copy, 2);
        Vec3 xyz3 = BakedQuadHelper.getXYZ(copy, 3);

        if (!isFinite(xyz0) || !isFinite(xyz1) || !isFinite(xyz2) || !isFinite(xyz3)) {
            return copy;
        }

        Vec3 uAxis = xyz3.add(xyz2).scale(0.5);
        Vec3 vAxis = xyz1.add(xyz2).scale(0.5);
        Vec3 center = xyz3.add(xyz2).add(xyz0).add(xyz1).scale(0.25);

        float uScale = scaleU(sprite, BakedQuadHelper.getU(copy, 3), BakedQuadHelper.getU(copy, 0), xyz3.distanceTo(xyz0));
        float vScale = scaleV(sprite, BakedQuadHelper.getV(copy, 1), BakedQuadHelper.getV(copy, 0), xyz1.distanceTo(xyz0));

        if (uScale == 0.0F) {
            uAxis = xyz1.add(xyz2).scale(0.5);
            vAxis = xyz3.add(xyz2).scale(0.5);
            uScale = scaleU(sprite, BakedQuadHelper.getU(copy, 1), BakedQuadHelper.getU(copy, 0), xyz1.distanceTo(xyz0));
            vScale = scaleV(sprite, BakedQuadHelper.getV(copy, 3), BakedQuadHelper.getV(copy, 0), xyz3.distanceTo(xyz0));
        }

        Vec3 normalizedUAxis = safeNormalize(uAxis.subtract(center));
        Vec3 normalizedVAxis = safeNormalize(vAxis.subtract(center));
        boolean canAdjustUv = normalizedUAxis != null && normalizedVAxis != null && Float.isFinite(uScale) && Float.isFinite(vScale);

        Vec3 min = new Vec3(crop.minX, crop.minY, crop.minZ);
        Vec3 max = new Vec3(crop.maxX, crop.maxY, crop.maxZ);

        for (int vertex = 0; vertex < 4; vertex++) {
            Vec3 xyz = BakedQuadHelper.getXYZ(copy, vertex);
            if (!isFinite(xyz)) {
                continue;
            }

            Vec3 newXyz = VecHelper.componentMin(max, VecHelper.componentMax(xyz, min));
            Vec3 diff = newXyz.subtract(xyz);

            if (canAdjustUv && diff.lengthSqr() > 0) {
                float u = BakedQuadHelper.getU(copy, vertex);
                float v = BakedQuadHelper.getV(copy, vertex);
                float uDiff = (float) normalizedUAxis.dot(diff) * uScale;
                float vDiff = (float) normalizedVAxis.dot(diff) * vScale;
                float newU = sprite.getU(getUnInterpolatedU(sprite, u) + uDiff);
                float newV = sprite.getV(getUnInterpolatedV(sprite, v) + vDiff);
                if (Float.isFinite(newU) && Float.isFinite(newV)) {
                    BakedQuadHelper.setU(copy, vertex, newU);
                    BakedQuadHelper.setV(copy, vertex, newV);
                }
            }

            Vec3 moved = newXyz.add(move);
            if (isFinite(moved)) {
                BakedQuadHelper.setXYZ(copy, vertex, moved);
            }
        }

        return copy;
    }

    private static float scaleU(TextureAtlasSprite sprite, float highU, float lowU, double distance) {
        if (distance <= EPSILON || !Double.isFinite(distance) || !Float.isFinite(highU) || !Float.isFinite(lowU)) {
            return 0.0F;
        }
        double scale = (getUnInterpolatedU(sprite, highU) - getUnInterpolatedU(sprite, lowU)) / distance;
        return Double.isFinite(scale) ? (float) Math.round(scale) : 0.0F;
    }

    private static float scaleV(TextureAtlasSprite sprite, float highV, float lowV, double distance) {
        if (distance <= EPSILON || !Double.isFinite(distance) || !Float.isFinite(highV) || !Float.isFinite(lowV)) {
            return 0.0F;
        }
        double scale = (getUnInterpolatedV(sprite, highV) - getUnInterpolatedV(sprite, lowV)) / distance;
        return Double.isFinite(scale) ? (float) Math.round(scale) : 0.0F;
    }

    private static Vec3 safeNormalize(Vec3 vector) {
        if (!isFinite(vector) || vector.lengthSqr() <= EPSILON * EPSILON) {
            return null;
        }
        Vec3 normalized = vector.normalize();
        return isFinite(normalized) ? normalized : null;
    }

    private static boolean isFinite(AABB box) {
        return box != null
            && Double.isFinite(box.minX) && Double.isFinite(box.minY) && Double.isFinite(box.minZ)
            && Double.isFinite(box.maxX) && Double.isFinite(box.maxY) && Double.isFinite(box.maxZ);
    }

    private static boolean isFinite(Vec3 vector) {
        return vector != null
            && Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }
}
