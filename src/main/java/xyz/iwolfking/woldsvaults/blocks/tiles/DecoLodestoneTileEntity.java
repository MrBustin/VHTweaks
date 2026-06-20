package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DecoLodestoneTileEntity extends BlockEntity {

    protected boolean consumed = false;
    public DecoLodestoneTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DECO_LODESTONE_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DecoLodestoneTileEntity tile) {
        if (level.isClientSide()) {
            tile.playEffects();
        }
    }


    public boolean isConsumed() {
        return this.consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
        this.sendUpdates();
    }

    public void sendUpdates() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            this.setChanged();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        if (this.getLevel() != null && !this.consumed) {
            BlockPos pos = this.getBlockPos();
            if (!this.isConsumed()) {
                if (this.getLevel().getGameTime() % 2L >= 1L) {
                    ParticleEngine mgr = Minecraft.getInstance().particleEngine;
                    float ringSize = 3.0F;

                    for(int i = 0; (float)i < ringSize; ++i) {
                        float angle = (float)(i + 1) * 3.1415927F / ringSize + (float)this.getLevel().getGameTime() / 7.0F;
                        float radius = 1.0F;
                        float x = (float)((double)((float)pos.getX() + 0.5F) + (double)radius * Math.cos((double)angle));
                        float y = (float)((double)((float)pos.getY() + 0.2F * (float)(3 * i + 1)) + Math.sin((double)angle) * 0.4000000059604645);
                        float z = (float)((double)((float)pos.getZ() + 0.5F) + (double)radius * Math.sin((double)angle));
                        Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, (double)x, (double)y, (double)z, 0.0, 0.0, 0.0);
                        if (fwParticle != null) {
                            fwParticle.setColor(0.76862746F, 0.13333334F, 0.9254902F);
                            fwParticle.setParticleSpeed(0.0, 0.0, 0.0);
                            fwParticle.setLifetime(10);
                        }
                    }

                }
            }
        }
    }

    protected void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("Consumed", this.consumed);
    }

    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.consumed = nbt.getBoolean("Consumed");
    }

    @Nonnull
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
