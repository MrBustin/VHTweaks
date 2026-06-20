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

public class HoyArmorLayers extends ArmorLayers {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head",
                    CubeListBuilder.create()
                            .texOffs(0, 36).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
                            .texOffs(72, 59).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(46, 23).addBox(-1.0F, -9.5F, -5.5F, 2.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
                            .texOffs(78, 48).addBox(4.0F, -6.0F, -4.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                            .texOffs(66, 75).addBox(0.5F, -6.0F, -7.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                            .texOffs(82, 12).addBox(-6.0F, -7.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(82, 0).addBox(-2.25F, -1.75F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(82, 75).addBox(-0.25F, -1.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.75F, -2.25F, -5.0F, 0.0F, -0.3927F, 0.0F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(82, 18).addBox(-1.75F, -1.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(46, 77).addBox(0.25F, -1.75F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.75F, -2.25F, -5.0F, 0.0F, 0.3927F, 0.0F));

            /* Body definition */
            PartDefinition body = partdefinition.addOrReplaceChild("body",
                    CubeListBuilder.create()
                            .texOffs(32, 45).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
                            .texOffs(68, 36).addBox(-4.0F, 3.0F, -5.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(82, 7).addBox(-2.0F, 11.0F, -3.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                            .texOffs(0, 23).addBox(-6.0F, 1.0F, -4.5F, 12.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
                            .texOffs(72, 63).addBox(2.0F, 3.0F, 4.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
                            .texOffs(68, 42).addBox(2.5F, 12.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(10, 83).addBox(-4.5F, 12.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(32, 77).addBox(-3.0F, 4.0F, 3.0F, 5.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(54, 75).addBox(-5.0F, 3.0F, 4.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F)
            );

            body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 77).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.25F, -3.25F, 0.3927F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, 7.75F, -5.0F, 18.0F, 0.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 15.5F, -1.1781F, 0.0F, 0.0F));

            /* Arm definitions */
            PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 59).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(0, 82).addBox(-5.0F, 6.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
            right_arm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 52).addBox(-1.0F, -3.5F, -3.0F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.25F, -0.5F, 0.0F, 0.0F, 0.3927F));

            PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 61).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(76, 81).addBox(3.0F, 6.5F, -1.5F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
            left_arm.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(56, 45).addBox(-1.0F, -3.5F, -3.0F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.25F, 0.5F, 3.1416F, 0.0F, 2.7489F));

            /* Leg definitions */
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(38, 61).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(66, 81).addBox(-2.2F, 6.0F, -4.0F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 66).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).texOffs(22, 52).addBox(-1.0F, 6.0F, -4.0F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

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

            PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -3.5F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 13.5F, 0.0F, 0.0F, 0.0F, -0.3927F));
            body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -3.5F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 13.5F, 0.0F, 0.0F, 0.0F, 0.3927F));
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).texOffs(10, 32).addBox(-1.25F, 2.0F, -3.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 11).addBox(-1.55F, 2.0F, -3.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

    }
}
