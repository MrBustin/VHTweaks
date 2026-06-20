package io.github.a1qs.vaultadditions.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class DummyArmorLayers extends ArmorLayers {
    @Override
    public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::createBodyLayer : MainLayer::createBodyLayer;
    }

    public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::new : MainLayer::new;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MainLayer extends ArmorLayers.MainLayer {
        public MainLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            return LayerDefinition.create(createBaseLayer(), 128, 128);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
        public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            return LayerDefinition.create(createBaseLayer(), 64, 64);
        }
    }
}