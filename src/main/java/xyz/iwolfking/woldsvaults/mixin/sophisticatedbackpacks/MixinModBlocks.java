package xyz.iwolfking.woldsvaults.mixin.sophisticatedbackpacks;

import net.minecraft.world.level.block.Block;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = ModBlocks.class, remap = false)
public class MixinModBlocks {

    @ModifyArg(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityType$Builder;of(Lnet/minecraft/world/level/block/entity/BlockEntityType$BlockEntitySupplier;[Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/entity/BlockEntityType$Builder;", remap = true), index = 1)
    private static Block[] injectNewBackpack(Block[] pValidBlocks) {
        ArrayList<Block> backpackList = new ArrayList<>(Arrays.stream(pValidBlocks).toList());
        backpackList.add(xyz.iwolfking.woldsvaults.init.ModBlocks.XL_BACKPACK);
        return backpackList.toArray(new Block[]{});
   }
}
