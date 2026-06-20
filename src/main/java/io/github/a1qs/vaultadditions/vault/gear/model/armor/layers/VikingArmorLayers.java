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

public class VikingArmorLayers extends ArmorLayers {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
                    .texOffs(16, 50).addBox(-1.0F, -8.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 65).addBox(-6.0F, -5.5F, -2.0F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(66, 14).addBox(-6.0F, -3.5F, 0.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(38, 65).addBox(5.5F, -3.5F, 0.0F, 1.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 45).addBox(5.5F, -5.5F, -2.0F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -8.5F, 1.0F, -0.4363F, 0.0F, 0.0F));

            /* Body definition */
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
                    .texOffs(32, 18).addBox(-4.0F, 3.0F, -4.5F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 16).addBox(-6.0F, -1.0F, 2.0F, 12.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 37).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 9).addBox(-2.0F, 5.0F, 2.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(50, 34).addBox(-4.0F, 4.0F, -4.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(50, 40).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.4168F, -1.6922F, 0.3491F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 12).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 4.5F, -1.1345F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 66).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 3.0F, -3.5F, 0.0F, 0.0F, 0.8727F));
            body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(64, 57).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 3.0F, -3.5F, 0.0F, 0.0F, -0.8727F));

            /* Arm definitions */
            PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 47).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            right_arm.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(44, 63).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 8.5F, 0.0F, 0.0F, 0.0F, -0.4363F));
            right_arm.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 41).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));
            right_arm.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(24, 25).addBox(-2.5F, -2.0F, -3.5F, 5.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -2.0F, -0.5F, 0.0F, 0.0F, -0.5236F));

            PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            left_arm.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(24, 47).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));
            left_arm.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(56, 63).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 8.5F, 0.0F, 0.0F, 0.0F, 0.4363F));
            left_arm.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

            /* Leg definitions */
            PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            right_leg.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(32, 56).addBox(-4.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 9.5F, -3.0F, 0.2618F, 0.0F, 0.0F));

            PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 56).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
            left_leg.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 9.5F, -3.0F, 0.2618F, 0.0F, 0.0F));


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

            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)).texOffs(24, 0).addBox(-2.0F, 9.5F, -4.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

    }
}
