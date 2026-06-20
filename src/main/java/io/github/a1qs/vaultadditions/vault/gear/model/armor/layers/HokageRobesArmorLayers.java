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

public class HokageRobesArmorLayers extends ArmorLayers {
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
            PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(61, 53).addBox(-2.5F, -0.625F, -4.275F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(67, 83).addBox(-2.0F, -2.0F, 0.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.35F, -4.25F, 0.0F, 0.0F, 0.7854F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-4.8041F, 1.125F, -1.4941F, 19.0F, 1.0F, 19.0F, new CubeDeformation(0.0F)).texOffs(0, 21).addBox(-3.8041F, 0.125F, -0.4941F, 17.0F, 1.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.3907F, -7.15F, -9.0059F, 0.0F, -0.7854F, 0.0F));
            head.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                    .texOffs(25, 70).addBox(7.1959F, 1.125F, 7.5059F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(34, 91).addBox(6.1959F, 1.875F, 6.5059F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(77, 0).addBox(4.1959F, 2.875F, 4.5059F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 57).addBox(2.1959F, 3.875F, 2.5059F, 11.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 40).addBox(0.1959F, 4.875F, 0.5059F, 15.0F, 1.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2694F, -12.9F, -11.1272F, 0.0F, -0.7854F, 0.0F));

            /* Body definition */
            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 70).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
                    .texOffs(77, 9).addBox(-4.5F, -0.75F, -2.5F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.2F))
                    .texOffs(78, 53).addBox(-4.5F, 0.25F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            /* Arm definitions */
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(98, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F))
                    .texOffs(96, 60).addBox(-3.7F, 6.3F, -2.55F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(96, 80).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F))
                    .texOffs(96, 70).addBox(-1.3F, 6.3F, -2.55F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

            /* Leg definitions */
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(84, 94).addBox(-2.1F, 0.7F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.35F))
                    .texOffs(106, 8).addBox(-2.1F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.31F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(67, 94).addBox(-2.0F, 0.7F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.35F))
                    .texOffs(106, 0).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.31F)), PartPose.offset(1.9F, 12.0F, 0.0F));

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

            partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 10.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(17, 7).addBox(-2.1F, 0.6F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
            partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, 0.6F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

    }
}
