package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.block.blockentity.PlayerTraderBlockEntity;
import io.github.a1qs.vaultadditions.container.LootStatueContainer;
import io.github.a1qs.vaultadditions.container.PlayerTraderContainer;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.CoinBlockItem;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class PlayerTraderBlock extends Block implements EntityBlock {

    public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 12, 0, 16, 16, 16),
            Block.box(2, 0, 2, 14, 12, 14));


    private record CoinDefinition(Item coinItem, @Nullable Item previousHigherDenomination, @Nullable Item nextLowerDenomination, int coinValue) {}
    private static Map<Item, PlayerTraderBlock.CoinDefinition> COIN_DEFINITIONS;

    public PlayerTraderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(new TextComponent("[NYI]").withStyle(ChatFormatting.RED));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return player.getAbilities().instabuild && super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pPlacer instanceof ServerPlayer player) {
            if (pLevel.getBlockEntity(pPos) instanceof PlayerTraderBlockEntity playerTraderEntity) {
                playerTraderEntity.setOwner(player.getUUID());
                playerTraderEntity.setTraderName(player.getName().getString());
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if(pLevel.getBlockEntity(pPos) instanceof PlayerTraderBlockEntity blockEntity) {
            if(pPlayer.getUUID().equals(blockEntity.getOwner())) {
                NetworkHooks.openGui((ServerPlayer) pPlayer, blockEntity, buffer -> buffer.writeBlockPos(pPos));
            }
        }


        if(pPlayer.isSecondaryUseActive() && pPlayer.getAbilities().instabuild) {
            if (pLevel.getBlockEntity(pPos) instanceof PlayerTraderBlockEntity blockEntity) {
                blockEntity.clear();
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }

        if (pPlayer.getAbilities().instabuild) {
            // Temporary setting of offers if the player is in creative
            ItemStack o = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack c = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
            if (!c.isEmpty() && !o.isEmpty()) {
                if (pLevel.getBlockEntity(pPos) instanceof PlayerTraderBlockEntity blockEntity) {
                    blockEntity.setOffer(OverSizedItemStack.of(o.copy()), OverSizedItemStack.of(c.copy()));
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }
        }

        if (pLevel.getBlockEntity(pPos) instanceof PlayerTraderBlockEntity blockEntity && pHand == InteractionHand.MAIN_HAND) {
            ItemStack offerStack = blockEntity.getOffer();

            if (!offerStack.isEmpty()) {

                return pPlayer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).map(itemHandler -> {
                    List<InventoryUtil.ItemAccess> allItems = List.of();
                    if (!pPlayer.isCreative()) {
                        allItems = InventoryUtil.findAllItems(pPlayer);
                        if (!hasEnoughCurrency(allItems, blockEntity.getCurrencyStack())) {
                            pPlayer.displayClientMessage(new TranslatableComponent("message.the_vault.shop_pedestal.fail",
                                    blockEntity.getCurrencyStack().getHoverName()), true);
                            return InteractionResult.sidedSuccess(pLevel.isClientSide);
                        }
                    }
                    if (!pPlayer.isCreative()) {
                        if(extractCurrency(pPlayer, allItems, blockEntity.getCurrencyStack(), (blockEntity.getCurrencyStack().getItem() instanceof CoinBlockItem))) {
                            pPlayer.displayClientMessage(new TranslatableComponent("message.the_vault.shop_pedestal.purchase",
                                    offerStack.getCount(), offerStack.getHoverName(), blockEntity.getCurrencyStack().getCount(), blockEntity.getCurrencyStack().getHoverName()), true);
                            blockEntity.clear(); // Clear the offer, TODO needs to be changed when players can add their offers to support multiple offers.
                        }
                    }
                    ItemHandlerHelper.giveItemToPlayer(pPlayer, offerStack.copy());

                    pLevel.playSound(null, pPos, SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 1, 1);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }).orElse(InteractionResult.PASS);
            }
            return InteractionResult.PASS;
        }


        return InteractionResult.PASS;
    }

    private boolean hasEnoughCurrency(List<InventoryUtil.ItemAccess> allItems, ItemStack currency) {
        if(currency.getItem() instanceof CoinBlockItem) {
            return getCoinDefinitions(currency.getItem()).map(priceCoinDefinition -> {
                int priceValue = priceCoinDefinition.coinValue() * currency.getCount();
                for (InventoryUtil.ItemAccess itemAccess : allItems) {
                    priceValue -= getCoinDefinitions(itemAccess.getStack().getItem())
                            .map(coinDefinition -> coinDefinition.coinValue() * itemAccess.getStack().getCount()).orElse(0);
                    if (priceValue <= 0) {
                        return true;
                    }
                }
                return false;
            }).orElse(false);
        } else {
            for(InventoryUtil.ItemAccess item : allItems) {
                if(currency.getItem() == item.getStack().getItem()) {
                    if(currency.getCount() <= item.getStack().getCount()) {
                        return true;
                    }
                }
            }
        }
        return false;


    }

    public boolean extractCurrency(Player player, List<InventoryUtil.ItemAccess> allItems, ItemStack price, boolean isCoin) {
        if(isCoin) {
            getCoinDefinitions(price.getItem()).ifPresent(priceCoinDefinition -> {
                int priceValue = priceCoinDefinition.coinValue() * price.getCount();

                priceValue = deductCoins(allItems, priceValue, priceCoinDefinition);

                if (priceValue > 0) {
                    priceValue = payUsingLowerDenominations(allItems, priceValue, priceCoinDefinition);
                    priceValue = payUsingHigherDenominations(allItems, priceValue, priceCoinDefinition);
                }

                if (priceValue < 0) {
                    int change = -priceValue;
                    returnChangeToPlayer(player, change);
                }
            });
        } else {
            for(InventoryUtil.ItemAccess item : allItems) {
                if(price.getItem() == item.getStack().getItem()) {
                    if(price.getCount() <= item.getStack().getCount()) {

                        item.setStack(ItemHandlerHelper.copyStackWithSize(item.getStack(), item.getStack().getCount() - price.getCount()));
                        return true;
                    }
                }
            }
        }


        return true;
    }

    private static void returnChangeToPlayer(Player player, int change) {
        while(change > 0) {
            for (PlayerTraderBlock.CoinDefinition definition : COIN_DEFINITIONS.values()) {
                if (definition.coinValue() <= change && change / definition.coinValue() < 9) {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(definition.coinItem(), change / definition.coinValue()));
                    change -= definition.coinValue() * (change / definition.coinValue());
                }
            }
        }
    }

    private int payUsingHigherDenominations(List<InventoryUtil.ItemAccess> allItems, int priceValue, PlayerTraderBlock.CoinDefinition coinDefinition) {
        while(priceValue > 0 && coinDefinition.previousHigherDenomination != null) {
            Optional<PlayerTraderBlock.CoinDefinition> higherCoinDefinition = getCoinDefinitions(coinDefinition.previousHigherDenomination);
            if (higherCoinDefinition.isPresent()) {
                coinDefinition = higherCoinDefinition.get();
                priceValue = deductCoins(allItems, priceValue, coinDefinition);
            }
        }
        return priceValue;
    }

    private int payUsingLowerDenominations(List<InventoryUtil.ItemAccess> allItems, int priceValue, PlayerTraderBlock.CoinDefinition coinDefinition) {
        while (priceValue > 0 && coinDefinition.nextLowerDenomination != null) {
            Optional<PlayerTraderBlock.CoinDefinition> lowerCoinDefinition = getCoinDefinitions(coinDefinition.nextLowerDenomination);
            if (lowerCoinDefinition.isPresent()) {
                coinDefinition = lowerCoinDefinition.get();
                priceValue = deductCoins(allItems, priceValue, coinDefinition);
            }
        }
        return priceValue;
    }

    private int deductCoins(List<InventoryUtil.ItemAccess> allItems, int priceValue, PlayerTraderBlock.CoinDefinition coinDefinition) {
        for (InventoryUtil.ItemAccess itemAccess : allItems) {
            ItemStack stack = itemAccess.getStack();
            if (stack.getItem() == coinDefinition.coinItem()) {

                int countToRemove = (int) Math.ceil(Math.min(priceValue, stack.getCount() * coinDefinition.coinValue()) / (double) coinDefinition.coinValue());
                if (countToRemove > 0) {
                    itemAccess.setStack(ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - countToRemove));
                    priceValue -= countToRemove * coinDefinition.coinValue();
                    if (priceValue <= 0) {
                        break;
                    }
                }
            }
        }

        return priceValue;
    }

    private static Optional<PlayerTraderBlock.CoinDefinition> getCoinDefinitions(Item coinBlock) {
        if (COIN_DEFINITIONS == null) {
            COIN_DEFINITIONS = new HashMap<>();
            COIN_DEFINITIONS.put(ModBlocks.VAULT_BRONZE, new PlayerTraderBlock.CoinDefinition(ModBlocks.VAULT_BRONZE, ModBlocks.VAULT_SILVER, null, 1));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_SILVER, new PlayerTraderBlock.CoinDefinition(ModBlocks.VAULT_SILVER, ModBlocks.VAULT_GOLD, ModBlocks.VAULT_BRONZE, 9));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_GOLD, new PlayerTraderBlock.CoinDefinition(ModBlocks.VAULT_GOLD, ModBlocks.VAULT_PLATINUM, ModBlocks.VAULT_SILVER, 81));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_PLATINUM, new PlayerTraderBlock.CoinDefinition(ModBlocks.VAULT_PLATINUM,null, ModBlocks.VAULT_GOLD, 729));
        }

        return Optional.ofNullable(COIN_DEFINITIONS.get(coinBlock));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PlayerTraderBlockEntity(pPos, pState);
    }
}
