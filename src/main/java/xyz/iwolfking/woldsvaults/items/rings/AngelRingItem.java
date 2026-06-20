package xyz.iwolfking.woldsvaults.items.rings;

import dev.denismasterherobrine.angelring.utils.AngelRingTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.Predicate;

public class AngelRingItem extends Item {
    public AngelRingItem() {
        super((new Properties()).stacksTo(1).tab(AngelRingTab.ANGELRING2));
    }

    private static final Predicate<ItemStack> ringItems = itemStack -> {
        return itemStack.getItem() instanceof AngelRingItem;
    };
    public static boolean isRingInCurioSlot(Player player) {

        List<SlotResult> slots =  CuriosApi.getCuriosHelper().findCurios(player, ringItems);

        return !slots.isEmpty();
    }
}
