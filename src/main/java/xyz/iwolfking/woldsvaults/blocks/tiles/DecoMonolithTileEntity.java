package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.entity.MonolithTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class DecoMonolithTileEntity extends MonolithTileEntity {
    public DecoMonolithTileEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DecoMonolithTileEntity tile) {
        if (level.isClientSide()) {
            tile.playEffects();
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        if (this.getLevel() != null) {
            BlockPos pos = this.getBlockPos();
            BlockState state = this.getBlockState();
            if (this.getLevel().getGameTime() % 1L == 0L) {
                ParticleEngine mgr = Minecraft.getInstance().particleEngine;
                if (state.getValue(MonolithBlock.STATE) == MonolithBlock.State.LIT) {
                    Random random = this.getLevel().getRandom();
                    Vec3 offset;
                    if (random.nextInt(5) == 0) {
                        offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                        this.getLevel().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.getY() + random.nextDouble() * 0.15000000596046448 + 0.550000011920929, (double)pos.getZ() + 0.5 + offset.z, offset.x / 120.0, random.nextDouble() * -0.005 + 0.075, offset.z / 120.0);
                    }

                    if (random.nextInt(2) == 0) {
                        offset = new Vec3(random.nextDouble() / 9.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 9.0 * (double)(random.nextBoolean() ? 1 : -1));
                        this.getLevel().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.getY() + 1.5499999523162842 + random.nextDouble() * 0.15000000596046448, (double)pos.getZ() + 0.5 + offset.z, offset.x / 120.0, random.nextDouble() * -0.005 + 0.075, offset.z / 120.0);
                    }

                    if (random.nextInt(15) == 0) {
                        offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                        this.getLevel().addParticle(ParticleTypes.LAVA, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.getY() + random.nextDouble() * 0.15000000596046448 + 0.550000011920929, (double)pos.getZ() + 0.5 + offset.z, offset.x / 12.0, random.nextDouble() * 0.1 + 0.1, offset.z / 12.0);
                    }
                }

            }
        }
    }
}
