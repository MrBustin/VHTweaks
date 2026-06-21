package xyz.iwolfking.woldsvaults.client.events;

import iskallia.vault.core.event.ForgeEvent;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.configs.Configs;
import xyz.iwolfking.woldsvaults.gear.effect.TransmogEffect;
import xyz.iwolfking.woldsvaults.util.ModelUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT})
public class TooltipEvent extends ForgeEvent<TooltipEvent, ItemTooltipEvent> {

    public TooltipEvent() {
    }

    protected TooltipEvent(TooltipEvent parent) {
        super(parent);
    }

    @Override
    public TooltipEvent createChild() {
        return new TooltipEvent(this);
    }

    @Override
    protected void initialize() {
        for(EventPriority priority : EventPriority.values()) {
            MinecraftForge.EVENT_BUS.addListener(priority, true, (Consumer<ItemTooltipEvent>) event -> {this.invoke(event);});
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void addArmorSetBuffs(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!(itemStack.getItem() instanceof VaultGearItem) || Configs.TRANSMOG_EFFECTS_CONFIG == null) {
            return;
        }

        List<Component> toolTip = event.getToolTip();
        DynamicModel<?> model = ModelUtil.getDynamicModel(itemStack, false);
        addEffectTooltip(toolTip, "Model Bonus:", Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(model));

        if (model instanceof ArmorPieceModel piece) {
            addEffectTooltip(toolTip, "Full Set Bonus:", Configs.TRANSMOG_EFFECTS_CONFIG.getEffects(piece.getArmorModel()));
        }
    }

    private static void addEffectTooltip(List<Component> toolTip, String type, List<TransmogEffect> effects) {
        if (effects.isEmpty()) {
            return;
        }

        List<Component> effectTooltip = new ArrayList<>();
        for (TransmogEffect effect : effects) {
            MutableComponent effectText = effect.getTooltip();
            if (effectText != null) {
                effectTooltip.add(new TextComponent("* ").withStyle(ChatFormatting.AQUA).append(effectText));
            }
        }

        if (!effectTooltip.isEmpty()) {
            toolTip.add(new TextComponent(""));
            toolTip.add(new TextComponent(type).withStyle(ChatFormatting.GREEN));
            toolTip.addAll(effectTooltip);
        }
    }

}
