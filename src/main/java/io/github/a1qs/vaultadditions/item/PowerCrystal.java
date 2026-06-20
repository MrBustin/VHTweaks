package io.github.a1qs.vaultadditions.item;

import io.github.a1qs.vaultadditions.block.GlobeExpanderBlock;
import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PowerCrystal extends Item {
    public PowerCrystal(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        if (VaultBarOverlay.vaultLevel >= 100) {
            tooltip.add(new TextComponent("Consume to gain one Power Point").withStyle(ChatFormatting.YELLOW));
        } else {
            tooltip.add(new TextComponent("I seem to be too weak to make use of this...").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.RED));
        }
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel) || isUsingInvalidBlock(level, player) || GlobeExpanderBlock.isCurrentlyInUse()) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack heldItemStack = player.getItemInHand(hand);
        PlayerVaultStatsData statsData = PlayerVaultStatsData.get(serverLevel);
        if (statsData.getVaultStats(player).getVaultLevel() < 100) {
            return InteractionResultHolder.fail(heldItemStack);
        }

        PlayerAdditionalVaultStatData additionalStatsData = PlayerAdditionalVaultStatData.get(serverLevel);
        int consumeAmount = player.isCrouching() ? heldItemStack.getCount() : 1;

        if (!player.getAbilities().instabuild) {
            heldItemStack.shrink(consumeAmount);
        }

        additionalStatsData.addPowerPoints((ServerPlayer) player, consumeAmount);
        player.awardStat(Stats.ITEM_USED.get(this), consumeAmount);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        return InteractionResultHolder.success(heldItemStack);
    }

    private boolean isUsingInvalidBlock(Level world, Player player) {
        BlockHitResult blockHitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
        return blockState.is(ModBlocks.GLOBE_EXPANDER.get());
    }
}
