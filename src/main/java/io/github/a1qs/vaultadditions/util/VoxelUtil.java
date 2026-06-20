package io.github.a1qs.vaultadditions.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelUtil {

    // Used to merge an array of Voxels together in an efficient manner
    public static VoxelShape mergeVoxelShapes(VoxelShape[] shape) {
        VoxelShape combinedShape = Shapes.empty();
        for(VoxelShape s : shape) {
            combinedShape = Shapes.join(combinedShape, s, BooleanOp.OR);
        }
        return combinedShape;
    }
    // Used to rotate a single VoxelShape depending on an angle
    public static VoxelShape rotateShape(VoxelShape shape, int angle) {
        return switch (angle) {
            case 90 -> rotateShape90(shape);
            case 180 -> rotateShape180(shape);
            case 270 -> rotateShape270(shape);
            default -> shape;
        };
    }


    private static VoxelShape rotateShape90(VoxelShape shape) {
        return rotateShapeGeneric(shape, aabb -> new AABB(1 - aabb.maxZ, aabb.minY, aabb.minX, 1 - aabb.minZ, aabb.maxY, aabb.maxX));
    }

    private static VoxelShape rotateShape180(VoxelShape shape) {
        return rotateShapeGeneric(shape, aabb -> new AABB(1 - aabb.maxX, aabb.minY, 1 - aabb.maxZ, 1 - aabb.minX, aabb.maxY, 1 - aabb.minZ));
    }

    private static VoxelShape rotateShape270(VoxelShape shape) {
        return rotateShapeGeneric(shape, aabb -> new AABB(aabb.minZ, aabb.minY, 1 - aabb.maxX, aabb.maxZ, aabb.maxY, 1 - aabb.minX));
    }

    private static VoxelShape rotateShapeGeneric(VoxelShape shape, java.util.function.Function<AABB, AABB> transform) {
        VoxelShape[] buffer = {Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[0] = Shapes.or(buffer[0], Shapes.create(transform.apply(new AABB(minX, minY, minZ, maxX, maxY, maxZ)))));
        return buffer[0];
    }

}
