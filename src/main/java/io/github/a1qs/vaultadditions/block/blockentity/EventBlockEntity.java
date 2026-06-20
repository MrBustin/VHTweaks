package io.github.a1qs.vaultadditions.block.blockentity;


import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.events.VaultAdditionsEvent;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.util.TimeUtil;
import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class EventBlockEntity extends BlockEntity {
    @Nonnull
    protected List<String> lines = new LinkedList<>();
    private int tickCounter = 0;

    public EventBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EVENT_BLOCK_ENTITY.get(), pos, state);

        if(this.level != null && !this.level.isClientSide()) {
            this.updateLines();
        }
    }

    @Nonnull
    public List<String> getLines() {
        return this.lines;
    }

    public void updateLines() {
        this.lines.clear();
        this.lines.add("{\"text\":\"Event Information\",\"bold\":true,\"underlined\":true,\"color\":\"yellow\"}");
        this.lines.add("");

        EventData data = EventData.get(ServerLifecycleHooks.getCurrentServer());
        if(data.isEventActive()) {
            VaultAdditionsEvent activeEvent = data.getActiveEvent();
            long remainingTime = data.getEventDuration();



            long seconds = (remainingTime / 20) % 60;
            long minutes = (remainingTime / (20 * 60)) % 60;
            long hours = (remainingTime / (20 * 60 * 60)) % 24;
            long days = remainingTime / (20 * 60 * 60 * 24);

            this.lines.add("[\"\",{\"text\":\"Event active until: \",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"" + days + "d " + hours + "h " + minutes + "m " + seconds + "s\"}]");
            this.lines.add(Component.Serializer.toJson(activeEvent.getEventDisplayMessage()));
            if(activeEvent.isCrystalSubmissionEvent()) this.lines.add("[\"\",{\"text\":\"Submitted Crystals: \",\"bold\":true},{\"text\":\"" + activeEvent.getCrystalsSubmitted() + "\",\"color\":\"yellow\"},{\"text\":\" /\",\"color\":\"white\"},{\"text\":\" " + activeEvent.getRequiredCrystals() + "\",\"color\":\"yellow\"}]");

        } else {
            if(!data.getNextScheduledEvent().equals("No upcoming events!")) {
                long targetTime = TimeUtil.convertToEpochMillis(data.getNextScheduledEvent());
                long currentTime = System.currentTimeMillis();
                long remainingTime = targetTime - currentTime;

                    long seconds = (remainingTime / 1000) % 60;
                    long minutes = (remainingTime / (1000 * 60)) % 60;
                    long hours = (remainingTime / (1000 * 60 * 60)) % 24;
                    long days = remainingTime / (1000 * 60 * 60 * 24);

                this.lines.add("[\"\",{\"text\":\"Next Event in: \",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"" + days + "d " + hours + "h " + minutes + "m " + seconds + "s\"}]");

            } else {
                this.lines.add("[\"\",{\"text\":\"Next Event in: \",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"" + data.getNextScheduledEvent() +"\"}]");
            }
        }


        this.setChanged();
    }


    public void loadFromNBT(CompoundTag nbt) {
        this.lines = NBTHelper.readList(nbt, "Lines", StringTag.class, StringTag::getAsString);
    }

    public void writeToEntityTag(CompoundTag nbt) {
        NBTHelper.writeCollection(nbt, "Lines", this.lines, StringTag.class, StringTag::valueOf);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        this.writeToEntityTag(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.loadFromNBT(pTag);
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (!level.isClientSide()) {
            if (blockEntity instanceof EventBlockEntity eventBlock) {
                if (eventBlock.tickCounter == 0) {
                    eventBlock.updateLines();
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
                }

                eventBlock.tickCounter = (eventBlock.tickCounter + 1) % 20;
            }
        }
    }
}
