package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.block.CoinPileBlock;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
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

import java.util.Random;

@Mixin(value = CoinPileBlock.class, remap = false)
public class MixinCoinLooting {

    @Inject(method = "generateLoot", at = @At("TAIL"))
    private void doubleCoinDrops(Level level, BlockPos pos, Player player, CallbackInfo ci) {
        float doubleChance = getCoinDoubleChance(player);
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

    private static float getCoinDoubleChance(Player player) {
        return AttributeSnapshotHelper.getInstance()
                .getSnapshot(player)
                .getAttributeValue(ModGearAttributes.COIN_DOUBLE_CHANCE, VaultGearAttributeTypeMerger.floatSum());
    }
}
