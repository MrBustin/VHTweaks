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

public class BokatanArmorLayers extends ArmorLayers {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F))
                    .texOffs(46, 36).addBox(-4.0F, -8.0F, -5.75F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 61).addBox(-6.0F, -7.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(22, 32).addBox(-6.0F, -14.0F, 0.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                    .texOffs(50, 24).addBox(-6.0F, -15.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(60, 0).addBox(-2.25F, -1.75F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(54, 62).addBox(-0.25F, -1.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.75F, -2.25F, -4.5F, 0.0F, -0.3927F, 0.3054F));

            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(60, 25).addBox(-1.75F, -1.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(46, 58).addBox(0.25F, -1.75F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.75F, -2.25F, -4.5F, 0.0F, 0.3927F, -0.3054F));

            /* Body definition */
            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.75F))
                    .texOffs(8, 61).addBox(-5.75F, 10.0F, -0.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(62, 7).addBox(3.5F, 10.0F, -0.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(62, 19).addBox(-5.75F, 10.0F, -3.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(62, 13).addBox(3.5F, 10.0F, -3.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(48, 0).addBox(2.0F, 3.0F, 4.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 58).addBox(2.5F, 12.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(26, 65).addBox(-4.5F, 12.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 54).addBox(-4.5F, 1.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(62, 62).addBox(2.5F, 1.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(54, 46).addBox(-3.0F, 4.0F, 3.0F, 5.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(50, 12).addBox(-5.0F, 3.0F, 4.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(54, 40).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.5F, -3.25F, 0.3927F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(46, 29).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.2F, -5.0F, -0.4363F, 0.0F, 0.0F));

            /* Arm definitions */

            PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)).texOffs(16, 58).addBox(-4.5F, 3.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            right_arm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(24, 29).addBox(-1.0F, -3.5F, -3.0F, 4.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.25F, -0.5F, 0.0F, 0.0F, 0.3927F));

            PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 42).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F)).texOffs(54, 54).addBox(2.5F, 3.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            left_arm.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -3.5F, -3.0F, 4.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.25F, 0.5F, 3.1416F, 0.0F, 2.7489F));

            /* Leg definitions */
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(38, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F))
                    .texOffs(36, 58).addBox(-1.45F, 6.0F, -4.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(42, 65).addBox(-1.45F, 4.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.74F))
                    .texOffs(34, 65).addBox(-1.5F, 4.0F, -3.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(26, 58).addBox(-1.5F, 6.0F, -4.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

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
