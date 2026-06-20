package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class GlobeExpanderBlockEntity extends BlockEntity {
    public int ticksSpinning = 0;
    public int ticksSpinningOld = 0;
    public final int maxSpinTicks = 65;
    private boolean isAnimating = false;
    private static boolean broadcastMessage = false;
    private static String playerName;
    private static double blocksExpanded;

    public GlobeExpanderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLOBE_EXPANDER_ENTITY.get(), pos, state);
    }

    public static <T extends BlockEntity> void clientTick(Level world, BlockPos pos, BlockState state, T tile) {
        if (tile instanceof GlobeExpanderBlockEntity expanderEntity) {
            if (expanderEntity.isAnimating && expanderEntity.ticksSpinning < expanderEntity.maxSpinTicks) {
                expanderEntity.ticksSpinningOld = expanderEntity.ticksSpinning;
                expanderEntity.ticksSpinning++;
            }
        }
    }

    public static <T extends BlockEntity> void serverTick(Level world, BlockPos pos, BlockState state, T tile) {
        if (tile instanceof GlobeExpanderBlockEntity expanderEntity) {
            if (expanderEntity.isAnimating && expanderEntity.ticksSpinning < expanderEntity.maxSpinTicks) {
                expanderEntity.ticksSpinningOld = expanderEntity.ticksSpinning;
                expanderEntity.ticksSpinning++;
                expanderEntity.setChanged();
            } else if (expanderEntity.ticksSpinning >= expanderEntity.maxSpinTicks) {
                expanderEntity.stopAnimation();
                world.playSound(null, pos, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.0f, 1.25f);
                BlockPos crystalPos = new BlockPos(pos.getX(), pos.getY() + 1.5, pos.getZ());
                world.levelEvent(2003, crystalPos,0);
                EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());

                if(!(data.isEventActive() && data.getActiveEvent().isCrystalSubmissionEvent())) {
                    MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
                    srv.getPlayerList().broadcastMessage(
                            new TextComponent("[World Border] " + playerName + " expanded the World border by " + blocksExpanded + " Blocks!")
                                    .withStyle(ChatFormatting.YELLOW),
                            ChatType.CHAT,
                            Util.NIL_UUID
                    );
                }
            }
        }
    }


    public void resetSpinTime() {
        this.ticksSpinningOld = 0;
        this.ticksSpinning = 0;
        this.isAnimating = true;
        this.setChanged();
    }

    public void stopAnimation() {
        this.isAnimating = false;
        this.ticksSpinning = 0;
        this.ticksSpinningOld = 0;


        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean shouldAnimate() {
        return isAnimating;
    }

    public void setBroadcastMessage(String playerName, double blocksExpanded) {
        this.broadcastMessage = true;
        this.playerName = playerName;
        this.blocksExpanded = blocksExpanded;
    }

    public boolean shouldBroadcastMessage() {
        return broadcastMessage;
    }

    public void clearBroadcastMessage() {
        this.broadcastMessage = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBlocksExpanded() {
        return blocksExpanded;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("IsAnimating", this.isAnimating);
        tag.putInt("TicksSpinning", this.ticksSpinning);
        tag.putInt("TicksSpinningOld", this.ticksSpinningOld);
    }

    public void load(CompoundTag tag) {
        super.load(tag);

        // Read animation state and ticks from NBT
        this.isAnimating = tag.getBoolean("IsAnimating");
        this.ticksSpinning = tag.getInt("TicksSpinning");
        this.ticksSpinningOld = tag.getInt("TicksSpinningOld");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
