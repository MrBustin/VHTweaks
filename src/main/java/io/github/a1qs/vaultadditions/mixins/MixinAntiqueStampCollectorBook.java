package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.antique.Antique;
import iskallia.vault.container.inventory.AntiqueCollectorBookContainer;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.AntiqueItem;
import iskallia.vault.item.AntiqueStampCollectorBook;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@Mixin(value = AntiqueStampCollectorBook.class)
public class MixinAntiqueStampCollectorBook {

    @Inject(method = "interceptPlayerInventoryItemAddition", at = @At("HEAD"), cancellable = true, remap = false)
    private static void curioSupport(Inventory playerInventory, ItemStack toAdd, CallbackInfoReturnable<Boolean> cir) {
        Player player = playerInventory.player;
        if (player.containerMenu instanceof AntiqueCollectorBookContainer) {
            cir.setReturnValue(false);
            return;
        }

        Antique antique = AntiqueItem.getAntique(toAdd);
        if (antique == null) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack antiqueBook = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.ANTIQUE_COLLECTOR_BOOK)
                .map(SlotResult::stack).orElse(ItemStack.EMPTY);

        if (!antiqueBook.isEmpty()) {
            AntiqueStampCollectorBook.StoredAntiques antiques = AntiqueStampCollectorBook.getStoredAntiques(antiqueBook);
            AntiqueStampCollectorBook.StoredAntiqueInfo info = antiques.getInfo(antique);
            int count = info.getCount();
            int result = Math.min(count + toAdd.getCount(), Integer.MAX_VALUE - 65);
            int added = result - count;
            info.setCount(result);
            toAdd.setCount(toAdd.getCount() - added);
            AntiqueStampCollectorBook.setStoredAntiques(antiqueBook, antiques);
            cir.setReturnValue(true);
        }
    }
}
