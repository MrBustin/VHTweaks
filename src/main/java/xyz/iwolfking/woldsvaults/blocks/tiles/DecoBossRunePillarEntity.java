package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.init.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class DecoBossRunePillarEntity extends BlockEntity {

    public static final BlockPos[] PARTICLE_POSITIONS = { BlockPos.ZERO.east(), BlockPos.ZERO.west(), BlockPos.ZERO.south(), BlockPos.ZERO.north(),
            BlockPos.ZERO.north().north(), BlockPos.ZERO.north().east(), BlockPos.ZERO.north().west(),
            BlockPos.ZERO.south().south(), BlockPos.ZERO.south().east(), BlockPos.ZERO.south().west(),
            BlockPos.ZERO.west().west(), BlockPos.ZERO.east().east() };

    public DecoBossRunePillarEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DECO_BOSS_RUNE_PILLAR_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tickClient(Level world, BlockPos pos, BlockState state, DecoBossRunePillarEntity tile) {
        RandomSource random = JavaRandom.ofNanoTime();
        BlockPos above = pos.above();
        Vec3 center = new Vec3(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5);

        for (BlockPos particlePos : PARTICLE_POSITIONS) {
            for (int offset = 0; offset < 3; ++offset) {
                if (random.nextInt(40) == 0) {
                    world.addParticle(ModParticles.BOSS_RUNE_CORE.get(), center.x, center.y, center.z,
                            -0.5F + random.nextFloat() + particlePos.getX(),
                            -2.0F + random.nextFloat() + particlePos.getY() + offset,
                            -0.5F + random.nextFloat() + particlePos.getZ()
                    );
                }
            }
        }

        if (random.nextInt(10) == 0) {
            Vec3 particlePos = new Vec3(above.getX() + 0.5F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F,
                    above.getY() - 0.25F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F,
                    above.getZ() + 0.5F + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 3.0F / 10.0F);
            world.addParticle(ModParticles.BOSS_RUNE_CORE.get(), particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0);
        }
    }
}
