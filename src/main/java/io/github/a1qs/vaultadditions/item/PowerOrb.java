package io.github.a1qs.vaultadditions.item;

import io.github.a1qs.vaultadditions.data.PlayerAdditionalVaultStatData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PowerOrb extends Item {
    public PowerOrb(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(new TextComponent("Grants a").withStyle(ChatFormatting.YELLOW)
                        .append(new TextComponent(" Power Point").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724414))))
                        .append(new TextComponent(" upon use.").withStyle(ChatFormatting.YELLOW))
        );
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            PlayerAdditionalVaultStatData statsData = PlayerAdditionalVaultStatData.get((ServerLevel) world);
            int amount = player.isCrouching() ? itemStack.getCount() : 1;
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(amount);
            }
            statsData.addPowerPoints((ServerPlayer) player, amount);
            player.awardStat(Stats.ITEM_USED.get(this), amount);
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }
}
