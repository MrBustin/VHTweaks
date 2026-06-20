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

public class SpaceMarineArmorLayers extends ArmorLayers {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
                    .texOffs(70, 31).addBox(-5.0F, -2.0F, -6.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(54, 88).addBox(-5.0F, 0.0F, -6.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(88, 15).addBox(0.0F, -2.0F, -6.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(78, 76).addBox(3.0F, -2.5F, -7.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 79).addBox(-6.0F, -2.5F, -7.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                    .texOffs(72, 0).addBox(-4.0F, -9.0F, -7.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(40, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(40, 9).addBox(-2.0F, -11.0F, -2.5F, 4.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(88, 19).addBox(0.0F, 0.0F, -6.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(28, 40).addBox(-0.5F, -7.5F, -7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 21).addBox(-1.0F, -8.5F, -7.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 89).addBox(-4.2682F, -1.25F, -1.1487F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 58).addBox(-1.2682F, -3.25F, -1.1487F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(88, 9).addBox(-2.2682F, -0.25F, -1.1487F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0747F, -3.75F, -6.097F, -0.1548F, 0.5618F, -0.083F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 61).addBox(-0.7318F, -3.25F, -1.1487F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(88, 17).addBox(-0.7318F, -1.25F, -1.1487F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(46, 88).addBox(-0.7318F, -0.25F, -1.1487F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0747F, -3.75F, -6.097F, -0.1548F, -0.5618F, 0.083F));

            /* Body definition */
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(54, 90).addBox(-1.5F, 0.0F, -9.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(22, 64).addBox(-1.0F, 2.0F, -9.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(28, 51).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
                    .texOffs(0, 0).addBox(-6.0F, 3.0F, -7.0F, 12.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 24).addBox(-7.0F, 1.0F, -8.0F, 14.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                    .texOffs(60, 47).addBox(-7.0F, -1.0F, -8.0F, 14.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(38, 89).addBox(6.0F, -1.0F, -7.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(78, 89).addBox(-7.0F, -1.0F, -7.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 13).addBox(-8.0F, -6.0F, 3.0F, 16.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(52, 51).addBox(-5.0F, 1.0F, 3.0F, 10.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(22, 67).addBox(-3.0F, -5.0F, -6.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.5F, 10.75F, -0.0436F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(64, 9).addBox(-2.0F, -3.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -2.5F, 8.75F, -0.5236F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(52, 60).addBox(-4.0F, -6.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 7.0F, -0.5236F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(80, 50).addBox(-2.0F, -5.0F, -2.5F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 4.5F, -0.3927F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(70, 21).addBox(-4.0F, -3.5F, -1.5F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.5F, -3.5F, 0.2182F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(90, 47).addBox(-2.5F, -1.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(14, 89).addBox(-2.0F, -2.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(46, 69).addBox(-1.5F, 0.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 2.0F, -8.0F, 0.0F, 0.0F, -0.3491F));
            body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(46, 67).addBox(-0.5F, 0.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(86, 89).addBox(-1.5F, -1.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(66, 88).addBox(-3.0F, -2.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 2.0F, -8.0F, 0.0F, 0.0F, 0.3491F));

            /* Arm definitions */
            PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 72).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            right_arm.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 58).addBox(-2.0F, -4.5F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 8.5F, 0.0F, 0.0F, 0.0F, -0.1745F));
            right_arm.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(32, 33).addBox(-5.5F, -7.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

            PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(46, 72).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            left_arm.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(60, 33).addBox(-2.0F, -5.5F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 9.5F, 0.0F, 0.0F, 0.0F, 0.1745F));
            left_arm.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -6.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0412F, -1.3066F, 0.0F, 0.0F, 0.0F, -0.3054F));


            /* Leg definitions */
            PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(86, 5).addBox(-2.45F, 11.0F, -5.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(62, 72).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
                    .texOffs(36, 79).addBox(-1.5F, 4.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

            right_leg.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(78, 86).addBox(-7.0F, -0.5F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.55F, 3.5F, -3.5F, 0.2182F, 0.0F, 0.0F));
            right_leg.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(82, 39).addBox(-6.0F, -2.5F, -0.5F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8F, 9.5F, -3.5F, 0.2182F, 0.0F, 0.0F));

            PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(36, 84).addBox(-1.55F, 4.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(78, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
                    .texOffs(72, 5).addBox(-2.5F, 11.0F, -5.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

            left_leg.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 88).addBox(-1.0F, -0.5F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 3.5F, -3.5F, 0.2182F, 0.0F, 0.0F));
            left_leg.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(82, 31).addBox(-2.0F, -2.5F, -0.5F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.5F, -3.5F, 0.2182F, 0.0F, 0.0F));

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

            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

    }
}
