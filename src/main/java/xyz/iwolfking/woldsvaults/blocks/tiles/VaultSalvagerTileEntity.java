package xyz.iwolfking.woldsvaults.blocks.tiles;

import com.google.common.collect.Lists;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.RecyclableItem;
import iskallia.vault.network.message.RecyclerParticleMessage;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.PacketDistributor;
import xyz.iwolfking.woldsvaults.api.inventory.SimpleOversizedSidedContainer;
import xyz.iwolfking.woldsvaults.blocks.containers.VaultSalvagerContainer;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class VaultSalvagerTileEntity extends BlockEntity implements MenuProvider {

    private static final Random rand = new Random();
    private final SalvagerInventory inventory = new SalvagerInventory(10);
    private UUID gearIdProcessing = null;
    private int processTick = 0;
    private LazyOptional<? extends IItemHandler>[] handlers;

    public VaultSalvagerTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlocks.VAULT_SALVAGER_ENTITY, pWorldPosition, pBlockState);
        this.handlers = SidedInvWrapper.create(this.inventory, new Direction[]{Direction.UP, Direction.DOWN});
    }

    public boolean stillValid(Player player) {
        return this.level != null && this.level.getBlockEntity(this.worldPosition) == this && this.inventory.stillValid(player);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, VaultSalvagerTileEntity tile) {
        if (!world.isClientSide()) {
            ejectOutputsToHandlerBelow(tile);
        }
        if (!world.isClientSide() && tile.gearIdProcessing != null) {
            if (!tile.canCraft()) {
                tile.resetProcess(world);
            } else {
                ++tile.processTick;
                if (tile.processTick >= 2) {
                    tile.finishCraft();
                }

                tile.setChanged();
                if (world instanceof ServerLevel) {
                    ServerLevel serverWorld = (ServerLevel)world;
                    serverWorld.sendBlockUpdated(pos, state, state, 3);
                }

            }
        }
    }

    private static boolean ejectOutputsToHandlerBelow(VaultSalvagerTileEntity tile) {
        if (tile.level == null) {
            return false;
        }
        Optional<Pair<IItemHandler, BlockEntity>> below = getItemHandlerBelow(tile);
        if (below.isEmpty()) {
            return false;
        }
        IItemHandler destination = below.get().getKey();
        if (isHandlerFull(destination)) {
            return false;
        }

        boolean movedAnything = false;
        for (int slot = 1; slot < tile.inventory.getContainerSize(); slot++) {
            ItemStack stackInSlot = tile.inventory.getItem(slot);
            if (stackInSlot.isEmpty()) {
                continue;
            }
            ItemStack originalSlotContents = stackInSlot.copy();
            int extractCount = Math.min(stackInSlot.getMaxStackSize(), stackInSlot.getCount());
            ItemStack extracted = tile.inventory.removeItem(slot, extractCount);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remainder = insertIntoHandler(destination, extracted);
            int movedCount = extractCount - remainder.getCount();

            if (movedCount > 0) {
                originalSlotContents.shrink(movedCount);
                tile.inventory.setItem(slot, originalSlotContents);
                movedAnything = true;
            } else {
                tile.inventory.setItem(slot, originalSlotContents);
            }

            if (isHandlerFull(destination)) {
                break;
            }
        }

        if (movedAnything) {
            tile.setChanged();
        }
        return movedAnything;
    }

    private static ItemStack insertIntoHandler(IItemHandler handler, ItemStack stack) {
        ItemStack remaining = stack;
        for (int slot = 0; slot < handler.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = handler.insertItem(slot, remaining, false);
        }
        return remaining;
    }

    private static boolean isHandlerFull(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stackInSlot = handler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private static Optional<Pair<IItemHandler, BlockEntity>> getItemHandlerBelow(VaultSalvagerTileEntity tile) {
        Level level = tile.level;
        if (level == null) {
            return Optional.empty();
        }
        BlockPos belowPos = tile.getBlockPos().below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.hasBlockEntity()) {
            return Optional.empty();
        }
        BlockEntity belowEntity = level.getBlockEntity(belowPos);
        if (belowEntity == null) {
            return Optional.empty();
        }
        return belowEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP)
                .map(handler -> ImmutablePair.of(handler, belowEntity));
    }

    private void finishCraft() {
        VaultRecyclerConfig.RecyclerOutput output = this.getRecipeOutput();
        if (output == null || this.level == null) return;

        this.level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F + new Random().nextFloat() * 0.25F, 0.75F + new Random().nextFloat() * 0.25F);

        ItemStack input = this.inventory.getItem(0).copy();
        float additionalChance = 0.0F;

        if (input.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(input);
            VaultGearRarity rarity = data.getRarity();

            // Handle Unique rarity - drop unique shard
            if (rarity == VaultGearRarity.UNIQUE) {
                this.inventory.removeItem(0, 1);
                MiscUtils.addStackToSlot(this.inventory, 1, new ItemStack(ModItems.UNIQUE_SHARD, 1));
                ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new RecyclerParticleMessage(this.getBlockPos()));
                return;
            }

            additionalChance = ModConfigs.VAULT_RECYCLER.getAdditionalOutputRarityChance(rarity);
            handleOutput(input, output, additionalChance, rarity);
        } else {
            handleOutput(input, output, additionalChance, null);
        }

        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new RecyclerParticleMessage(this.getBlockPos()));
    }

    private void handleOutput(ItemStack input, VaultRecyclerConfig.RecyclerOutput output, float additionalChance, VaultGearRarity rarity) {
        this.inventory.removeItem(0, 1);

        if (input.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(input);
            boolean isCrafted = data.hasAttribute(ModGearAttributes.CRAFTED_BY) || data.getFirstValue(ModGearAttributes.CRAFTED_BY).isPresent();
            boolean isLegendary = data.get(ModGearAttributes.IS_LEGENDARY, VaultGearAttributeTypeMerger.anyTrue());
            MiscUtils.addStackToSlot(this.inventory, 1,
                    this.getUseRelatedOutput(input, output.generateMainOutput(additionalChance)));
            MiscUtils.addStackToSlot(this.inventory, 2,
                    this.getUseRelatedOutput(input, output.generateExtraOutput1(input, additionalChance, rarity, isCrafted, isLegendary)));
            ItemStack extraOutput2 = output.generateExtraOutput2(input, additionalChance, rarity, isCrafted, isLegendary);
            ItemStack matchedStack = output.getExtraOutput2Matching();

            for (int slot = 3; slot <= 8; slot++) {
                if (MiscUtils.canFullyMergeIntoSlot(this.inventory, slot, matchedStack)) {
                    MiscUtils.addStackToSlot(this.inventory, slot,
                            this.getUseRelatedOutput(input, extraOutput2));
                    return;
                }
            }
            MiscUtils.addStackToSlot(this.inventory, 9,
                    this.getUseRelatedOutput(input, extraOutput2));
        } else {
            MiscUtils.addStackToSlot(this.inventory, 1,
                    this.getUseRelatedOutput(input, output.generateMainOutput(additionalChance)));
            MiscUtils.addStackToSlot(this.inventory, 2,
                    this.getUseRelatedOutput(input, output.generateExtraOutput1(input, additionalChance)));
            ItemStack extraOutput2 = output.generateExtraOutput2(input, additionalChance);
            ItemStack matchedStack = output.getExtraOutput2Matching();
            for (int slot = 3; slot <= 8; slot++) {
                if (MiscUtils.canFullyMergeIntoSlot(this.inventory, slot, matchedStack)) {
                    MiscUtils.addStackToSlot(this.inventory, slot,
                            this.getUseRelatedOutput(input, extraOutput2));
                    return;
                }
            }
            MiscUtils.addStackToSlot(this.inventory, 9,
                    this.getUseRelatedOutput(input, extraOutput2));
        }
    }


    private ItemStack getUseRelatedOutput(ItemStack input, ItemStack output) {
        float chance = this.getResultPercentage(input);
        float out = (float)output.getCount() * chance;
        int resultCount = Mth.floor(out);
        if (resultCount < 1 && out > 0.0F && rand.nextFloat() < out) {
            ++resultCount;
        }

        ItemStack copyOut = output.copy();
        copyOut.setCount(resultCount);
        return copyOut;
    }

    private boolean canCraft() {
        VaultRecyclerConfig.RecyclerOutput output = this.getRecipeOutput();
        if (output == null) {
            return false;
        }

        // Special handling for Unique gear
        ItemStack input = this.inventory.getItem(0);
        if (input.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(input);
            VaultGearRarity rarity = data.getRarity();

            // If it's Unique rarity, only check if unique shard can fit in slot 1
            if (rarity == VaultGearRarity.UNIQUE) {
                return MiscUtils.canFullyMergeIntoSlot(this.inventory, 1, new ItemStack(ModItems.UNIQUE_SHARD, 1));
            }
        }

        // Normal recycling checks
        if(!MiscUtils.canFullyMergeIntoSlot(this.inventory, 1, output.getMainOutputMatching())) {
            return false;
        }
        else if(!MiscUtils.canFullyMergeIntoSlot(this.inventory, 2, output.getExtraOutput1Matching())) {
            return false;
        }
        else if(!MiscUtils.canFullyMergeIntoSlot(this.inventory, 3, output.getExtraOutput2Matching())
                && !MiscUtils.canFullyMergeIntoSlot(this.inventory, 4, output.getExtraOutput2Matching())
                && !MiscUtils.canFullyMergeIntoSlot(this.inventory, 5, output.getExtraOutput2Matching())
                && !MiscUtils.canFullyMergeIntoSlot(this.inventory, 6, output.getExtraOutput2Matching())
                && !MiscUtils.canFullyMergeIntoSlot(this.inventory, 7, output.getExtraOutput2Matching())
                && !MiscUtils.canFullyMergeIntoSlot(this.inventory, 8, output.getExtraOutput2Matching())) {
            return MiscUtils.canFullyMergeIntoSlot(this.inventory, 9, output.getExtraOutput2Matching());
        }
        return MiscUtils.canFullyMergeIntoSlot(this.inventory, 9, output.getExtraOutput2Matching());
    }

    @Nullable
    private VaultRecyclerConfig.RecyclerOutput getRecipeOutput() {
        ItemStack input = this.inventory.getItem(0);
        if (!this.isValidInput(input)) {
            return null;
        } else {
            Item var3 = input.getItem();
            if (var3 instanceof RecyclableItem) {
                RecyclableItem recyclableItem = (RecyclableItem)var3;
                return recyclableItem.getOutput(input);
            } else {
                return null;
            }
        }
    }

    private void triggerItemChange() {
        ItemStack input = this.inventory.getItem(0);
        if (!this.isValidInput(input)) {
            this.resetProcess((Level)null);
        } else {
            Item var3 = input.getItem();
            if (var3 instanceof RecyclableItem) {
                RecyclableItem recyclableItem = (RecyclableItem)var3;
                recyclableItem.getUuid(input).ifPresent((itemId) -> {
                    if ((this.gearIdProcessing == null || !this.gearIdProcessing.equals(itemId)) && this.canCraft()) {
                        this.startProcess((Level)null, itemId);
                    }

                });
            }
        }
    }

    public boolean isValidInput(ItemStack input) {
        Item var3 = input.getItem();
        if (var3 instanceof RecyclableItem recyclableItem) {
            return recyclableItem.isValidInput(input);
        } else {
            return false;
        }
    }

    public float getResultPercentage(ItemStack input) {
        Item var3 = input.getItem();
        if (var3 instanceof RecyclableItem recyclableItem) {
            return recyclableItem.getResultPercentage(input);
        } else {
            return 0.0F;
        }
    }

    private void resetProcess(@Nullable Level world) {
        this.startProcess(world, (UUID)null);
    }

    private void startProcess(@Nullable Level world, UUID id) {
        int prevTick = this.processTick;
        this.gearIdProcessing = id;
        this.processTick = 0;
        this.setChanged();
        if (prevTick != this.processTick && world instanceof ServerLevel serverWorld) {
            serverWorld.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }

    }

    public float getProgressPercent() {
        return (float)this.processTick / 2;
    }

    public SalvagerInventory getInventory() {
        return this.inventory;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        NBTHelper.deserializeSimpleContainer( this.inventory, tag.getList("inventory", 10));
        this.processTick = tag.getInt("processTick");
        if (tag.hasUUID("gearIdProcessing")) {
            this.gearIdProcessing = tag.getUUID("gearIdProcessing");
        }

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inventory));
        tag.putInt("processTick", this.processTick);
        if (this.gearIdProcessing != null) {
            tag.putUUID("gearIdProcessing", this.gearIdProcessing);
        }

    }

    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return this.getLevel() == null ? null : new VaultSalvagerContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP) {
                return this.handlers[0].cast();
            }

            if (facing == Direction.DOWN) {
                return this.handlers[1].cast();
            }
        }

        return super.getCapability(capability, facing);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        Arrays.stream(this.handlers).forEach(LazyOptional::invalidate);
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this.inventory, new Direction[]{Direction.UP, Direction.DOWN});
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnRecycleParticles(BlockPos pos) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            int i;
            Random random;
            Vec3 offset;
            for(i = 0; i < 4; ++i) {
                random = level.getRandom();
                offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                level.addParticle(ParticleTypes.LAVA, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.getY() + random.nextDouble() * 0.15000000596046448 + 0.25, (double)pos.getZ() + 0.5 + offset.z, offset.x / 2.0, random.nextDouble() * 0.1, offset.z / 2.0);
            }

            for(i = 0; i < 3; ++i) {
                random = level.getRandom();
                offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                level.addParticle(ParticleTypes.LAVA, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.above().getY() + random.nextDouble() * 0.15000000596046448, (double)pos.getZ() + 0.5 + offset.z, offset.x / 20.0, random.nextDouble() * 0.2, offset.z / 20.0);
            }

            for(i = 0; i < 3; ++i) {
                random = level.getRandom();
                offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                level.addParticle(ParticleTypes.SMOKE, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.above().getY() + random.nextDouble() * 0.15000000596046448, (double)pos.getZ() + 0.5 + offset.z, offset.x / 20.0, random.nextDouble() * 0.2, offset.z / 20.0);
            }

            for(i = 0; i < 3; ++i) {
                random = level.getRandom();
                offset = new Vec3(random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1));
                level.addParticle(ParticleTypes.LARGE_SMOKE, true, (double)pos.getX() + 0.5 + offset.x, (double)pos.above().getY() + random.nextDouble() * 0.15000000596046448, (double)pos.getZ() + 0.5 + offset.z, offset.x / 10.0, random.nextDouble() * 0.05, offset.z / 10.0);
            }

        }
    }

    public class SalvagerInventory extends SimpleOversizedSidedContainer {

        public SalvagerInventory(int size) {
            super(size);
        }

        public List<Direction> getAccessibleSlots(int slot) {
            return slot == 0 ? Lists.newArrayList(new Direction[]{Direction.UP}) : Lists.newArrayList(new Direction[]{Direction.DOWN});
        }

        public boolean canPlaceItem(int slot, ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            } else {
                if (slot == 0) {
                    Item var4 = stack.getItem();
                    if (var4 instanceof RecyclableItem) {
                        RecyclableItem recyclableItem = (RecyclableItem)var4;
                        return recyclableItem.isValidInput(stack);
                    }
                }

                return false;
            }
        }

        public void setChanged() {
            super.setChanged();
            VaultSalvagerTileEntity.this.setChanged();
            VaultSalvagerTileEntity.this.triggerItemChange();
        }
    }
}
