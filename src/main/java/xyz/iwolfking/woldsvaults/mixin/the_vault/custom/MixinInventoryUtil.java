package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.inventory.WoldInventoryUtil;
import xyz.iwolfking.woldsvaults.items.ItemScavengerPouch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Mixin(value = InventoryUtil.class, remap = false)
public abstract class MixinInventoryUtil {

    @Shadow
    @Final
    @Mutable
    private static Set<Function<InventoryUtil.ItemAccess, List<InventoryUtil.ItemAccess>>> CONTENT_ACCESSORS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void appendScavPouchAccessor(CallbackInfo ci) {
        Set<Function<InventoryUtil.ItemAccess, List<InventoryUtil.ItemAccess>>> accessors = new LinkedHashSet<>(CONTENT_ACCESSORS);
        accessors.add(WoldInventoryUtil::getScavPouchAccess);
        CONTENT_ACCESSORS = Collections.unmodifiableSet(accessors);
    }

    @Inject(method = "makeItemsRotten(Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;doesRotten(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static void clearScavPouch(Player player, CallbackInfo ci, @Local ItemStack stack) {
        if(stack.getItem() instanceof ItemScavengerPouch) {
            ItemScavengerPouch.setStoredStacks(stack, new ArrayList<>());
        }
    }
}
