package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

import java.util.Optional;
import java.util.Random;

@Mixin(value = CoinPileBlock.class, remap = false)
public class MixinCoinLooting {

    @Inject(method = "generateLoot", at = @At("TAIL"))
    private void doubleCoinDrops(Level level, BlockPos pos, Player player, CallbackInfo ci) {
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof VaultGearItem)) {
            return;
        }

        float doubleChance = getCoinDoubleChance(heldItem);
        if (doubleChance > 0.0f) {
            Random random = level.getRandom();
            float roll = random.nextFloat();

            if (roll < doubleChance) {
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
                var blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof iskallia.vault.block.entity.CoinPilesTileEntity te) {
                    if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        java.util.List<ItemStack> additionalLoot = te.generateLoot(serverPlayer);
                        if (level instanceof ServerLevel) {
                            additionalLoot.forEach((stack) -> {
                                CoinPileBlock.popResource(level, pos, stack);
                            });
                        }
                    }
                }
            }
        }
    }

    private static float getCoinDoubleChance(ItemStack stack) {
        if(stack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(stack);
            Optional<Float> coinDoubleChance = data.getFirstValue(ModGearAttributes.COIN_DOUBLE_CHANCE);
            if(coinDoubleChance.isPresent()) {
                return coinDoubleChance.get();
            }
        }

        return 0.0f;
    }
}