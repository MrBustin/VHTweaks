package xyz.iwolfking.woldsvaults.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class PlagueArmorLayers extends ArmorLayers{

    @Override
    public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
        // Boots only use MainLayer — no leggings layer needed
        return MainLayer::createBodyLayer;
    }

    @Override
    public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
        return MainLayer::new;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MainLayer extends ArmorLayers.MainLayer {

        public MainLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createBaseLayer();
            PartDefinition partdefinition = meshdefinition.getRoot();

            /* Left Boot definition */
            // NOTE: The part must be named "left_leg" so Vault Hunters attaches it to the
            // correct bone. The geometry below is taken directly from plague_boots left_shoe.
            PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
                    CubeListBuilder.create()
                            .texOffs(0, 0).addBox(-1.9F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
                            .texOffs(16, 5).addBox(0.15F, 7.2F, -2.825F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(1.9F, 12.0F, 0.0F));

            left_leg.addOrReplaceChild("cube_r1",
                    CubeListBuilder.create().texOffs(4, 17).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.225F, 7.2F, -2.25F, -0.4169F, 0.0F, -1.5745F));

            left_leg.addOrReplaceChild("cube_r2",
                    CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(1.125F, 7.2F, -2.325F, -0.4169F, 0.0F, 1.5745F));

            left_leg.addOrReplaceChild("cube_r3",
                    CubeListBuilder.create().texOffs(16, 8).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.15F, 8.025F, -2.175F, 0.6109F, 0.0F, 0.0F));

            left_leg.addOrReplaceChild("cube_r4",
                    CubeListBuilder.create().texOffs(16, 7).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.15F, 7.275F, -2.3F, -0.6109F, 0.0F, 0.0F));

            left_leg.addOrReplaceChild("cube_r5",
                    CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -1.0F, -3.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.45F, 7.8F, -1.4F, 1.5866F, -0.0074F, 0.4363F));

            left_leg.addOrReplaceChild("cube_r6",
                    CubeListBuilder.create().texOffs(8, 14).addBox(0.0F, -1.0F, -3.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(-0.2F, 8.2F, -1.4F, 1.5866F, -0.0074F, 0.4363F));

            left_leg.addOrReplaceChild("cube_r7",
                    CubeListBuilder.create()
                            .texOffs(16, 2).addBox(0.0F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                            .texOffs(8, 9).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(1.6F, 10.0F, 0.0F, 0.829F, 0.0F, 0.0F));

            left_leg.addOrReplaceChild("cube_r8",
                    CubeListBuilder.create().texOffs(0, 9).addBox(0.5F, -1.0F, -1.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(1.15F, 10.0F, -1.85F, -0.633F, 1.5496F, -2.3784F));

            left_leg.addOrReplaceChild("cube_r9",
                    CubeListBuilder.create().texOffs(0, 9).addBox(0.5F, -1.0F, -1.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(2.6F, 10.1F, 0.0F, 0.7714F, 0.3413F, -0.3315F));

            /* Right Boot definition */
            // NOTE: Must be named "right_leg" for the same reason as above.
            PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
                    CubeListBuilder.create()
                            .texOffs(0, 0).mirror().addBox(-2.1F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
                            .texOffs(16, 5).mirror().addBox(-1.15F, 7.2F, -2.825F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offset(-1.9F, 12.0F, 0.0F));

            right_leg.addOrReplaceChild("cube_r10",
                    CubeListBuilder.create().texOffs(0, 9).mirror().addBox(-0.5F, -1.0F, -1.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-1.15F, 10.0F, -1.85F, -0.633F, -1.5496F, 2.3784F));

            right_leg.addOrReplaceChild("cube_r11",
                    CubeListBuilder.create().texOffs(0, 9).mirror().addBox(-0.5F, -1.0F, -1.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-2.6F, 10.1F, 0.0F, 0.7714F, -0.3413F, 0.3315F));

            right_leg.addOrReplaceChild("cube_r12",
                    CubeListBuilder.create().texOffs(16, 8).mirror().addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-0.15F, 8.025F, -2.175F, 0.6109F, 0.0F, 0.0F));

            right_leg.addOrReplaceChild("cube_r13",
                    CubeListBuilder.create().texOffs(4, 17).mirror().addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-0.225F, 7.2F, -2.25F, -0.4169F, 0.0F, 1.5745F));

            right_leg.addOrReplaceChild("cube_r14",
                    CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-1.125F, 7.2F, -2.325F, -0.4169F, 0.0F, -1.5745F));

            right_leg.addOrReplaceChild("cube_r15",
                    CubeListBuilder.create().texOffs(16, 7).mirror().addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-0.15F, 7.275F, -2.3F, -0.6109F, 0.0F, 0.0F));

            right_leg.addOrReplaceChild("cube_r16",
                    CubeListBuilder.create().texOffs(16, 0).mirror().addBox(-3.0F, -1.0F, -3.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-0.45F, 7.8F, -1.4F, 1.5866F, 0.0074F, -0.4363F));

            right_leg.addOrReplaceChild("cube_r17",
                    CubeListBuilder.create().texOffs(8, 14).mirror().addBox(-1.0F, -1.0F, -3.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(0.2F, 8.2F, -1.4F, 1.5866F, 0.0074F, -0.4363F));

            right_leg.addOrReplaceChild("cube_r18",
                    CubeListBuilder.create()
                            .texOffs(16, 2).mirror().addBox(-1.0F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                            .texOffs(8, 9).mirror().addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                    PartPose.offsetAndRotation(-1.6F, 10.0F, 0.0F, 0.829F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }
    }
}
