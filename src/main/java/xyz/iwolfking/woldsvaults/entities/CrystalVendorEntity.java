package xyz.iwolfking.woldsvaults.entities;


import iskallia.vault.world.data.PlayerGreedTreeData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import xyz.iwolfking.woldsvaults.container.CrystalTraderContainer;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;
import xyz.iwolfking.woldsvaults.util.CrystalTraderCurrencyHelper;

import javax.annotation.Nullable;

public class CrystalVendorEntity extends PathfinderMob {

    public CrystalVendorEntity(EntityType<? extends CrystalVendorEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    public InteractionResult mobInteract(Player player, InteractionHand pHand) {
        if (!this.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            int greedTier = PlayerGreedTreeData.get(serverPlayer.getLevel()).getGreedTier(serverPlayer);
            NetworkHooks.openGui(serverPlayer, new MenuProvider() {
                public Component getDisplayName() {
                    return new TextComponent("Mr. White");
                }

                @Nullable
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                    return new CrystalTraderContainer(windowId, player, CrystalVendorEntity.this.getUUID());
                }
            }, (buf) -> {
                PlayerCrystalTraderData traderData = PlayerCrystalTraderData.get(serverPlayer.getLevel());
                int playerVaultLevel = PlayerVaultStatsData.get(serverPlayer.getLevel()).getVaultStats(serverPlayer).getVaultLevel();
                buf.writeUUID(this.getUUID());
                traderData.writeOffers(this.getUUID(), greedTier, playerVaultLevel, buf);
                int reputation = PlayerGreedTreeData.get(serverPlayer.getLevel()).getGreedReputation(serverPlayer);
                buf.writeInt(reputation);
                buf.writeInt(traderData.getResetCost(this.getUUID()));
                buf.writeInt(CrystalTraderCurrencyHelper.getCurrencyCount(serverPlayer));
            });
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }







    public int getAmbientSoundInterval() {
        return 400;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }
}
