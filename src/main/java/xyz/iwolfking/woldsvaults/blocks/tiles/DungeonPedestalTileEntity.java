package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.block.base.LootableTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class DungeonPedestalTileEntity extends LootableTileEntity {
    private static final Random rand = new Random();
    private ItemStack contained;

    public DungeonPedestalTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
        this.contained = ItemStack.EMPTY;
    }

    public AABB getRenderBoundingBox() {
        return (new AABB(this.worldPosition, this.worldPosition.offset(1, 1, 1))).inflate(5.0);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DungeonPedestalTileEntity tile) {
        if (world.isClientSide()) {
            clientTick(world, pos, state, tile);
        } else {
            if (tile.contained.isEmpty() && tile.getLootTable() != null) {
                List<ItemStack> generatedStacks = tile.generateLoot((ServerPlayer)null);
                tile.setLootTable((ResourceLocation)null);
                tile.contained = (ItemStack)generatedStacks.stream().filter((stack) -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
                tile.setChanged();
                world.sendBlockUpdated(pos, state, state, 3);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientTick(Level world, BlockPos pos, BlockState state, DungeonPedestalTileEntity tile) {
        if (!rand.nextBoolean()) {
            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            double x = (double)pos.getX() + 0.5 + (double)(rand.nextFloat() * 2.0F * (float)(rand.nextBoolean() ? 1 : -1));
            double y = (double)pos.getY() + 2.0 + (double)(rand.nextFloat() * 2.0F * (float)(rand.nextBoolean() ? 1 : -1));
            double z = (double)pos.getZ() + 0.5 + (double)(rand.nextFloat() * 2.0F * (float)(rand.nextBoolean() ? 1 : -1));
            float hueGold = 0.125F;
            int color = Color.HSBtoRGB(hueGold, 0.2F + rand.nextFloat() * 0.6F, 1.0F);
            if (rand.nextInt(5) == 0) {
                x = (double)pos.getX() + 0.5;
                y = (double)pos.getY() + 1.5;
                z = (double)pos.getZ() + 0.5;
            }

            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;
            SimpleAnimatedParticle particle = (SimpleAnimatedParticle)engine.createParticle(ParticleTypes.FIREWORK.getType(), x, y, z, (double)(rand.nextFloat() * 0.02F * (float)(rand.nextBoolean() ? 1 : -1)), (double)(rand.nextFloat() * 0.04F), (double)(rand.nextFloat() * 0.02F * (float)(rand.nextBoolean() ? 1 : -1)));
            particle.setColor(r, g, b);
        }
    }

    public ItemStack getContained() {
        return this.contained.copy();
    }

    public void setContained(ItemStack contained) {
        this.contained = contained;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.contained = ItemStack.of(tag.getCompound("contained"));
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("contained", this.contained.serializeNBT());
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
