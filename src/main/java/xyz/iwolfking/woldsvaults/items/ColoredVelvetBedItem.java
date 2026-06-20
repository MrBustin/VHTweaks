package xyz.iwolfking.woldsvaults.items;

import xyz.iwolfking.woldsvaults.client.renderers.ColoredVelvetBedISTER;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ColoredVelvetBedItem extends BedItem {
    public ColoredVelvetBedItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(ColoredVelvetBedISTER.INSTANCE);
    }
}
