package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.container.PlayerTraderContainer;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import iskallia.vault.container.oversized.OverSizedItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerTraderBlockEntity extends BlockEntity implements MenuProvider {
    private OverSizedItemStack offer = OverSizedItemStack.EMPTY;
    private OverSizedItemStack currency = OverSizedItemStack.EMPTY;
    private UUID ownerUUID;
    private String traderName;


    public PlayerTraderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLAYER_TRADER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.offer = OverSizedItemStack.deserialize(tag.getCompound("offer"));
        this.currency = OverSizedItemStack.deserialize(tag.getCompound("currencyStack"));
        this.ownerUUID = tag.getUUID("ownerUUID");
        this.traderName = tag.getString("traderName");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("offer", this.offer.serialize());
        tag.put("currencyStack", this.currency.serialize());
        tag.putUUID("ownerUUID", this.ownerUUID);
        tag.putString("traderName", this.traderName);
    }



    public ItemStack getOffer() {
        return this.offer.overSizedStack().copy();
    }

    public ItemStack getCurrencyStack() {
        return this.currency.overSizedStack().copy();
    }

    public void setOwner(UUID owner) {
        this.ownerUUID = owner;
    }

    public UUID getOwner() {
        return ownerUUID;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getTraderName() {
        return traderName;
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

    public void clear() {
        this.offer = OverSizedItemStack.EMPTY;
        this.currency = OverSizedItemStack.EMPTY;
        this.updateBlock();
    }

    public void setOffer(OverSizedItemStack offer, OverSizedItemStack currency) {
        this.offer = offer;
        this.currency = currency;
        this.updateBlock();
    }

    public void updateBlock() {
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("menu.vaultadditions.player_trader");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        if (this.getLevel() == null) {
            return null;
        }
        return new PlayerTraderContainer(pContainerId, this.getLevel(), this.getBlockPos(), pInventory);
    }
}
