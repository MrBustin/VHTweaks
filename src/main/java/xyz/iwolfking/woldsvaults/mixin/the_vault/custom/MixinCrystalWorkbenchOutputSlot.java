package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.container.CrystalWorkbenchContainer;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.recipe.anvil.AnvilExecutor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.util.CrystalSizePowerHelper;

import java.util.List;

@Mixin(targets = "iskallia.vault.container.CrystalWorkbenchContainer$4", remap = false)
public class MixinCrystalWorkbenchOutputSlot {
    @Inject(method = "m_142406_", at = @At("HEAD"))
    private void rollCrystalSizePowerOnRealTake(Player player, ItemStack output, CallbackInfo ci) {
        CrystalWorkbenchContainer container = getCrystalWorkbenchContainer();
        if (container == null) {
            return;
        }

        AnvilExecutor.Result result = container.getEntity().getExecutor(player.getGameProfile().getId());
        if (result == null || output.isEmpty()) {
            return;
        }

        List<ItemStack> ingredients = container.getEntity().getIngredients().getContents();
        int refundSize = 0;
        for (int slot = 0; slot < ingredients.size(); slot++) {
            if (!result.hasUsedSlot(slot)) {
                continue;
            }

            ItemStack ingredient = ingredients.get(slot);
            if (!(ingredient.getItem() instanceof InfusedCatalystItem)) {
                continue;
            }

            Integer catalystSize = InfusedCatalystItem.getSize(ingredient).orElse(null);
            if (catalystSize != null && catalystSize > 0 && CrystalSizePowerHelper.shouldNotConsumeCapacity(player)) {
                refundSize += catalystSize;
            }
        }

        CrystalSizePowerHelper.refundCapacityCost(output, refundSize);
    }

    private CrystalWorkbenchContainer getCrystalWorkbenchContainer() {
        try {
            java.lang.reflect.Field field = this.getClass().getDeclaredField("this$0");
            field.setAccessible(true);
            Object value = field.get(this);
            return value instanceof CrystalWorkbenchContainer container ? container : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
