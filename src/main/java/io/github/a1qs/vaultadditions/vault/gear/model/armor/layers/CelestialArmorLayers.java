package io.github.a1qs.vaultadditions.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class CelestialArmorLayers extends ArmorLayers {
    @Override
    public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::createBodyLayer : MainLayer::createBodyLayer;
    }

    @Override
    public VaultArmorLayerSupplier<? extends BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::new : MainLayer::new;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MainLayer extends ArmorLayers.MainLayer {
        public MainLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createBaseLayer();
            PartDefinition partdefinition = meshdefinition.getRoot();

            /* Head definition */
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(56, 19).addBox(-2.0F, -1.0F, -1.5F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 7.75F, -0.48F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 51).addBox(-3.0F, -2.5F, -1.5F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.75F, 5.5F, -0.2182F, 0.0F, 0.0F));

            /* Body definition */
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 11).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.75F))
                    .texOffs(28, 0).addBox(-6.0F, -2.0F, 1.0F, 12.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 35).addBox(5.0F, -8.0F, 3.0F, 10.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 38).addBox(-15.0F, -8.0F, 3.0F, 10.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, -4.0F, -3.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 7.0F, -3.0F, -1.3526F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 27).addBox(-4.0F, -4.0F, -3.0F, 9.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -3.0F, -0.9163F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -11.0F, 0.0F, 14.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 6.0F, 0.2182F, 0.0F, 0.0F));

            /* Arm definitions */
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(20, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(36, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)), PartPose.offset(5.0F, 2.0F, 0.0F));

            /* Leg definitions */
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 51).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(52, 35).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)), PartPose.offset(1.9F, 12.0F, 0.0F));


            return LayerDefinition.create(meshdefinition, 128, 128);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
        public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createBaseLayer();
            PartDefinition partdefinition = meshdefinition.getRoot();

            partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
                    .texOffs(24, 12).addBox(-2.0F, 10.0F, -3.5F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 0).addBox(-3.0F, 11.0F, -3.0F, 6.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 0).addBox(-3.0F, 11.0F, 3.0F, 6.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 12).addBox(-2.0F, 10.0F, 1.5F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );

            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

    }
}
