package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.block.StatueCauldronBlock;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.item.LootStatueBlockItem;
import io.github.a1qs.vaultadditions.util.UsernameProvider;
import iskallia.vault.init.ModBlocks;
// REMOVED: import iskallia.vault.init.ModConfigs; // No longer needed
import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class StatueCauldronBlockEntity extends BlockEntity {
    private UUID owner;
    private int statueCount;
    private int requiredAmount;
    private List<String> names = new ArrayList<>();

    private static final Map<String, Integer> ITEM_RECYCLING_VALUES;

    static {
        ITEM_RECYCLING_VALUES = new HashMap<>();
        ITEM_RECYCLING_VALUES.put("vaultadditions:loot_statue_vault", 2);
        ITEM_RECYCLING_VALUES.put("vaultadditions:loot_statue_gift", 4);
        ITEM_RECYCLING_VALUES.put("vaultadditions:loot_statue_gift_mega", 6);
        ITEM_RECYCLING_VALUES.put("vaultadditions:loot_statue_arena", 8);
    }

    private static final Predicate<ItemEntity> itemPredicate = (itemEntity) -> {
        return itemEntity.getItem().getItem() instanceof LootStatueBlockItem;
    };

    public StatueCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STATUE_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    public List<String> getNames() {
        return this.names;
    }

    public void setNames(ListTag nameList) {
        this.names.clear();
        for (int i = 0; i < nameList.size(); i++) {
            CompoundTag tag = (CompoundTag) nameList.get(i);
            this.names.add(tag.getString("name" + i));
        }
        sendUpdates();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        sendUpdates();
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setStatueCount(int statueCount) {
        this.statueCount = statueCount;
        sendUpdates();
    }

    public int getStatueCount() {
        return this.statueCount;
    }

    public void setRequiredAmount(int requiredAmount) {
        this.requiredAmount = requiredAmount;
        sendUpdates();
    }

    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    public void addName(String name) {
        this.names.add(name);
        sendUpdates();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if(!level.isClientSide()) {
            if(blockEntity instanceof StatueCauldronBlockEntity be) {
                if(blockState.getValue(StatueCauldronBlock.LEVEL) == 3) {

                    List<ItemEntity> statues = level.getEntitiesOfClass(ItemEntity.class, (new AABB(blockPos)).inflate(1.0, 1.0, 1.0), itemPredicate);


                    for(ItemEntity itemEntity : statues) {
                        if (be.getStatueCount() >= be.getRequiredAmount()) break;

                        ItemStack stack = itemEntity.getItem();
                        String registryName = stack.getItem().getRegistryName().toString();
                        int d = ITEM_RECYCLING_VALUES.getOrDefault(registryName, 0);

                        if(d > 0) {
                            be.setStatueCount(d + be.getStatueCount());
                            if(LootStatueBlockItem.getStatueName(stack) != null) be.addName(LootStatueBlockItem.getStatueName(stack));
                            itemEntity.remove(Entity.RemovalReason.DISCARDED);
                            bubbleCauldron((ServerLevel) level, blockPos);
                        }
                    }

                    if (be.getStatueCount() >= be.getRequiredAmount()) {
                        List<String> nameList = new ArrayList<>(be.getNames());
                        Collections.shuffle(nameList);
                        String name = nameList.isEmpty() ? UsernameProvider.getRandomUsername() : nameList.get(0);

                        ItemStack statue = new ItemStack(ModBlocks.LOOT_STATUE);
                        statue.getOrCreateTagElement("BlockEntityTag").putString("PlayerNickname", name);

                        ItemEntity itemEntity = new ItemEntity(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, statue);

                        level.addFreshEntity(itemEntity);

                        level.setBlock(blockPos, io.github.a1qs.vaultadditions.init.ModBlocks.STATUE_CAULDRON.get().defaultBlockState().setValue(StatueCauldronBlock.LEVEL, 1) ,3);
                        be.setStatueCount(0);
                        be.getNames().clear();
                        be.sendUpdates();
                    }
                }
            }
        }
    }

    private static void bubbleCauldron(ServerLevel world, BlockPos pos) {
        int particleCount = 100;
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ModSounds.CAULDRON_BUBBLES_SFX, SoundSource.MASTER, 1.0F, (float)Math.random());
        world.sendParticles(ParticleTypes.WITCH, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, particleCount, 0.5, 0.5, 0.5, 0.0);
    }


    @Override
    protected void saveAdditional(CompoundTag pTag) {
        if(this.owner != null) {
            pTag.putUUID("Owner", owner);
        }

        ListTag nameList = new ListTag();
        if (this.names != null && !this.names.isEmpty()) {
            int i = 0;
            for (String name : this.names) {
                CompoundTag nameNbt = new CompoundTag();
                nameNbt.putString("name" + i++, name);
                nameList.add(nameNbt);
            }
        }

        pTag.put("NameList", nameList);
        pTag.putInt("StatueCount", this.statueCount);
        pTag.putInt("RequiredAmount", this.requiredAmount);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.hasUUID("Owner")) {
            this.owner = pTag.getUUID("Owner");
        }

        ListTag nameList = pTag.getList("NameList", Tag.TAG_COMPOUND);
        int i = 0;
        this.names.clear();
        for(Tag tag : nameList) {
            CompoundTag compoundTag = (CompoundTag) tag;
            this.names.add(compoundTag.getString("name" + i++));
        }

        this.statueCount = pTag.getInt("StatueCount");
        this.requiredAmount = pTag.getInt("RequiredAmount");
        super.load(pTag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();
        if(this.owner != null) {
            compoundTag.putUUID("Owner", owner);
        }

        ListTag nameList = new ListTag();
        if (this.names != null && !this.names.isEmpty()) {
            int i = 0;
            for (String name : this.names) {
                CompoundTag nameNbt = new CompoundTag();
                nameNbt.putString("name" + i++, name);
                nameList.add(nameNbt);
            }
        }

        compoundTag.put("NameList", nameList);
        compoundTag.putInt("StatueCount", this.statueCount);
        compoundTag.putInt("RequiredAmount", this.requiredAmount);
        return compoundTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        this.handleUpdateTag(tag);
    }

    public void sendUpdates() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            this.setChanged();
        }
    }
}