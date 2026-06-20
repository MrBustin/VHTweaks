package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.client.particles.GridGatewayParticle;
import iskallia.vault.init.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import java.awt.*;
import java.util.Random;

public class DecoGridGatewayTileEntity extends BlockEntity {
    private static final int GRAY_COLOR = -11184811;
    private int completedBingos = 10; // Default to maximum effect

    public DecoGridGatewayTileEntity(BlockPos pPos, BlockState pState) {
        super(ModBlocks.DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE, pPos, pState);
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.completedBingos = nbt.getInt("completedBingos");
    }

    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("completedBingos", this.completedBingos);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, DecoGridGatewayTileEntity pBlockEntity) {
        int completedBingos = pBlockEntity.completedBingos;
        if (completedBingos > 0) {
            addBottomSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 0);
            addBottomSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 1);
        }

        if (completedBingos > 1) {
            addBottomSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 2);
            addBottomSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 3);
        }

        if (completedBingos > 2) {
            addSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 4, false);
        }

        if (completedBingos > 3) {
            addSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 5, false);
        }

        if (completedBingos > 4) {
            addSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 6, false);
        }

        if (completedBingos > 5) {
            addSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 7, false);
        }

        if (completedBingos > 6) {
            addSideParticles(pPos, Direction.EAST, pBlockEntity, completedBingos, 8, true);
        }

        if (completedBingos > 7) {
            addSideParticles(pPos, Direction.WEST, pBlockEntity, completedBingos, 9, true);
        }

        if (completedBingos > 8) {
            addSideParticles(pPos, Direction.NORTH, pBlockEntity, completedBingos, 10, true);
        }

        if (completedBingos > 9) {
            addSideParticles(pPos, Direction.SOUTH, pBlockEntity, completedBingos, 11, true);
        }

        addTopParticles(pPos, pBlockEntity, completedBingos);
    }

    @OnlyIn(Dist.CLIENT)
    private static void addTopParticles(BlockPos pos, DecoGridGatewayTileEntity be, int completedBingos) {
        Random rnd = be.level.getRandom();
        if (!(rnd.nextFloat() > 0.3F)) {
            for(int i = 0; i < completedBingos; ++i) {
                double randomX = (double)pos.getX() + 0.5 + rnd.nextDouble(0.3) - 0.15;
                double randomZ = (double)pos.getZ() + 0.5 + rnd.nextDouble(0.3) - 0.15;
                double randomY = (double)(pos.getY() + 1) + rnd.nextDouble(0.1);
                Particle particle = Minecraft.getInstance().particleEngine.createParticle((ParticleOptions)ModParticles.GRID_GATEWAY.get(), randomX, randomY, randomZ, 0.04 * (double)rnd.nextFloat() - 0.02, 0.05 * (double)rnd.nextFloat(), 0.04 * (double)rnd.nextFloat() - 0.02);
                if (particle instanceof GridGatewayParticle) {
                    GridGatewayParticle gridGatewayParticle = (GridGatewayParticle)particle;
                    gridGatewayParticle.setColor(be.getTintColor(12, completedBingos));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addSideParticles(BlockPos pos, Direction direction, DecoGridGatewayTileEntity be, int completedBingos, int tintIndex, boolean top) {
        Random rnd = be.level.getRandom();
        if (!(rnd.nextFloat() > 0.1F)) {
            double randomX = (double)pos.getX() + 0.5 + (direction.getStepX() == 0 ? rnd.nextDouble(0.6) - 0.3 : (double)direction.getStepX() * (0.25 + rnd.nextDouble(0.12)));
            double randomZ = (double)pos.getZ() + 0.5 + (direction.getStepZ() == 0 ? rnd.nextDouble(0.6) - 0.3 : (double)direction.getStepZ() * (0.25 + rnd.nextDouble(0.12)));
            double randomY = (double)pos.getY() + (top ? 0.575 : 0.2) + rnd.nextDouble(0.35);
            addParticle(be, completedBingos, tintIndex, randomX, randomY, randomZ);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addBottomSideParticles(BlockPos pos, Direction direction, DecoGridGatewayTileEntity be, int completedBingos, int tintIndex) {
        Random rnd = be.level.getRandom();
        if (!(rnd.nextFloat() > 0.1F)) {
            double randomX = (double)pos.getX() + 0.5 + (direction.getStepX() == 0 ? rnd.nextDouble(0.7) - 0.35 : (double)direction.getStepX() * (0.4 + rnd.nextDouble(0.1)));
            double randomZ = (double)pos.getZ() + 0.5 + (direction.getStepZ() == 0 ? rnd.nextDouble(0.7) - 0.35 : (double)direction.getStepZ() * (0.4 + rnd.nextDouble(0.1)));
            double randomY = (double)pos.getY() + 0.1 + rnd.nextDouble(0.1);
            addParticle(be, completedBingos, tintIndex, randomX, randomY, randomZ);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addParticle(DecoGridGatewayTileEntity be, int completedBingos, int tintIndex, double randomX, double randomY, double randomZ) {
        Particle particle = Minecraft.getInstance().particleEngine.createParticle((ParticleOptions)ModParticles.GRID_GATEWAY.get(), randomX, randomY, randomZ, 0.0, 0.0, 0.0);
        if (particle instanceof GridGatewayParticle gridGatewayParticle) {
            gridGatewayParticle.setColor(be.getTintColor(tintIndex, completedBingos));
        }
    }

    public int getTintColor(int tintIndex, DecoGridGatewayTileEntity tile) {
        return this.getTintColor(tintIndex, tile.completedBingos);
    }

    public int getTintColor(int tintIndex, int completedBingos) {
        if (completedBingos > 0 && tintIndex == 12) {
            float transition = (float)(System.currentTimeMillis() % 10000L) / 10000.0F;
            return Color.getHSBColor(transition, 1.0F, 0.7F + 0.3F * (float)completedBingos / 10.0F).getRGB();
        } else if ((tintIndex != 0 && tintIndex != 1 || completedBingos >= 1) && (tintIndex != 2 && tintIndex != 3 || completedBingos >= 2) && (tintIndex <= 3 || tintIndex - 1 <= completedBingos)) {
            byte var10000;
            switch (tintIndex) {
                case 0:
                case 1:
                    var10000 = 0;
                    break;
                case 2:
                case 3:
                    var10000 = 5;
                    break;
                case 4:
                    var10000 = 1;
                    break;
                case 5:
                    var10000 = 2;
                    break;
                case 6:
                    var10000 = 6;
                    break;
                case 7:
                    var10000 = 7;
                    break;
                case 8:
                    var10000 = 9;
                    break;
                case 9:
                    var10000 = 8;
                    break;
                case 10:
                    var10000 = 4;
                    break;
                case 11:
                    var10000 = 3;
                    break;
                default:
                    var10000 = 0;
            }

            int colorShift = var10000;
            float transition = (float)(System.currentTimeMillis() % 20000L + (long)(colorShift * 1000)) / 20000.0F;
            return Color.getHSBColor(transition, 1.0F, 1.0F).getRGB();
        } else {
            return GRAY_COLOR;
        }
    }

    public int getLight() {
        return (int)((float)this.completedBingos / 10.0F * 15.0F);
    }
}
