package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.block.ColoredVelvetBed;
import io.github.a1qs.vaultadditions.block.blockentity.ColoredVelvetBedBlockEntity;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ColoredVelvetBedRenderer implements BlockEntityRenderer<ColoredVelvetBedBlockEntity> {
    public static final ModelLayerLocation HEAD_LAYER_LOCATION = new ModelLayerLocation(VaultAdditions.id("head_layer_location"), "main");
    public static final ModelLayerLocation FOOT_LAYER_LOCATION = new ModelLayerLocation(VaultAdditions.id("foot_layer_location"), "main");
    private final ModelPart headRoot;
    private final ModelPart footRoot;

    public ColoredVelvetBedRenderer(BlockEntityRendererProvider.Context pContext) {
        this.headRoot = pContext.bakeLayer(HEAD_LAYER_LOCATION);
        this.footRoot = pContext.bakeLayer(FOOT_LAYER_LOCATION);
    }


    public static LayerDefinition createHeadLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -24.0F, 0.0F, 16.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pillow_r1 = bb_main.addOrReplaceChild("pillow_r1", CubeListBuilder.create().texOffs(29, 44).addBox(-6.0F, 0.5F, -3.25F, 12.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -17.5F, -2.25F, 1.309F, 0.0F, 3.1416F));

        PartDefinition blacket_fluff_r1 = bb_main.addOrReplaceChild("blacket_fluff_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-15.0F, -8.0F, 6.5F, 17.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 44).addBox(-11.5F, -10.5F, -1.5F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -22.5F, 7.5F, 1.5708F, 0.0F, 3.1416F));

        PartDefinition right_leg_r1 = bb_main.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(50, 12).addBox(-14.5F, 0.5F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -22.5F, -4.5F, 1.5708F, 0.0F, 3.1416F));

        PartDefinition left_leg_r1 = bb_main.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(50, 12).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -22.5F, -4.5F, 1.5708F, 0.0F, 1.5708F));

        PartDefinition right_leg_r2 = bb_main.addOrReplaceChild("right_leg_r2", CubeListBuilder.create().texOffs(50, 0).addBox(-14.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -22.5F, 7.5F, 1.5708F, 0.0F, 3.1416F));

        PartDefinition left_leg_r2 = bb_main.addOrReplaceChild("left_leg_r2", CubeListBuilder.create().texOffs(50, 0).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -22.5F, 7.5F, 1.5708F, 0.0F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(((float)Math.PI / 2F), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), PartPose.rotation(((float)Math.PI / 2F), 0.0F, ((float)Math.PI * 1.5F)));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void render(ColoredVelvetBedBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Level level = pBlockEntity.getLevel();
        if (level != null) {
            BlockState blockstate = pBlockEntity.getBlockState();

            if (blockstate.getBlock() instanceof ColoredVelvetBed coloredVelvetBed) {
                DyeColor color = coloredVelvetBed.getColor();
                ResourceLocation textureLocation = VaultAdditions.id("entity/bed/velvet_bed_" + color.getName());
                Material material = new Material(TextureAtlas.LOCATION_BLOCKS, textureLocation);

                // Combine light levels
                DoubleBlockCombiner.NeighborCombineResult<? extends ColoredVelvetBedBlockEntity> neighborcombineresult =
                        DoubleBlockCombiner.combineWithNeigbour(ModBlockEntities.COLORED_VELVET_BED_BLOCK_ENTITY.get(), BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, blockstate, level, pBlockEntity.getBlockPos(),
                                (p_112202_, p_112203_) -> false);
                int i = neighborcombineresult.apply(new BrightnessCombiner<>()).get(pPackedLight);

                this.renderPiece(pPoseStack, pBufferSource, blockstate.getValue(BedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot, blockstate.getValue(BedBlock.FACING), material, i, pPackedOverlay, false);
            }
        } else {
            DyeColor color = pBlockEntity.getColor();

            ResourceLocation textureLocation = VaultAdditions.id("entity/bed/velvet_bed_" + color.getName());
            Material material = new Material(TextureAtlas.LOCATION_BLOCKS, textureLocation);

            this.renderPiece(pPoseStack, pBufferSource, this.headRoot, Direction.SOUTH, material, pPackedLight, pPackedOverlay, false);
            this.renderPiece(pPoseStack, pBufferSource, this.footRoot, Direction.SOUTH, material, pPackedLight, pPackedOverlay, true);
        }

    }

    private void renderPiece(PoseStack pPoseStack, MultiBufferSource pBufferSource, ModelPart pModelPart, Direction pDirection, Material pMaterial, int pPackedLight, int pPackedOverlay, boolean pFoot) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.5625D, pFoot ? -1.0D : 0.0D);
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        pPoseStack.translate(0.5D, 0.5D, 0.5D);
        pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F + pDirection.toYRot()));
        pPoseStack.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer vertexconsumer = pMaterial.buffer(pBufferSource, RenderType::entitySolid);
        pModelPart.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }


}
