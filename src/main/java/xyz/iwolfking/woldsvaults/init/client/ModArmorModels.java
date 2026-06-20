package xyz.iwolfking.woldsvaults.init.client;


import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.gear.model.armor.layers.PlagueArmorLayers;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModArmorModels {
    public ModArmorModels() {
    }

    public static class Armor {
        public static final ArmorModel PLAGUE;

        public Armor() {
        }


        static {
            PLAGUE = ((new ArmorModel(VaultMod.id("gear/armor/plague"), "Plaguewalker"))
                    .properties((new DynamicModelProperties()).allowTransmogrification().discoverOnRoll()))
                    .usingLayers(new PlagueArmorLayers())
                    .addSlot(EquipmentSlot.FEET);
        }
    }
}
