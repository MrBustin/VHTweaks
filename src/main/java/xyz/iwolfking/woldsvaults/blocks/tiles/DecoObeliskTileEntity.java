package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.block.ObeliskBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.blocks.DecoObeliskBlock;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


import java.util.Random;

public class DecoObeliskTileEntity extends BlockEntity {
    private static final Random rand = new Random();

    public DecoObeliskTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DecoObeliskTileEntity tile) {
        if (!level.isClientSide()) {
            BlockState up = level.getBlockState(pos.above());
            if (!(up.getBlock() instanceof DecoObeliskBlock)) {
                level.setBlockAndUpdate(pos.above(), (BlockState) ModBlocks.DECO_OBELISK_BLOCK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.UPPER));
            }
        } else {
            tile.playEffects();
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        if (this.getLevel() != null) {
            BlockPos pos = this.getBlockPos();
            BlockState state = this.getBlockState();
            if (this.getLevel().getGameTime() % 5L == 0L) {
                ParticleEngine mgr = Minecraft.getInstance().particleEngine;
                int count;
                double x;
                double y;
                double z;
                Particle fwParticle;
                if ((Boolean)state.getValue(ObeliskBlock.FILLED)) {
                    for(count = 0; count < 3; ++count) {
                        x = (double)pos.getX() - 0.25 + (double)rand.nextFloat() * 1.5;
                        y = (double)((float)pos.getY() + rand.nextFloat() * 3.0F);
                        z = (double)pos.getZ() - 0.25 + (double)rand.nextFloat() * 1.5;
                        fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                        if (fwParticle == null) {
                            return;
                        }

                        fwParticle.setColor(0.4F, 0.0F, 0.0F);
                    }
                } else {
                    for(count = 0; count < 5; ++count) {
                        x = (double)((float)pos.getX() + rand.nextFloat());
                        y = (double)((float)pos.getY() + rand.nextFloat() * 10.0F);
                        z = (double)((float)pos.getZ() + rand.nextFloat());
                        fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                        if (fwParticle == null) {
                            return;
                        }

                        fwParticle.setLifetime((int)((float)fwParticle.getLifetime() * 1.5F));
                    }
                }

            }
        }
    }
}
