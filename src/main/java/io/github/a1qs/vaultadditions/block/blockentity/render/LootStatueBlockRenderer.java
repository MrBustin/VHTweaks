package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.block.LootStatueBlock;
import io.github.a1qs.vaultadditions.block.blockentity.LootStatueBlockEntity;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.util.SkinProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.StringUtils;

public class LootStatueBlockRenderer implements BlockEntityRenderer<LootStatueBlockEntity> {
    private static StatuePlayerModel PLAYER_MODEL;
    private final Minecraft mc = Minecraft.getInstance();
    private final ItemRenderer itemRenderer;
    private static final ResourceLocation STONE_SKIN = VaultMod.id("textures/entity/stoneskin.png");

    public LootStatueBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = this.mc.getItemRenderer();
        PLAYER_MODEL = new StatuePlayerModel(context);
    }

    @Override
    public void render(LootStatueBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {

        BlockState blockState = blockEntity.getBlockState();
        Direction direction = blockState.getValue(LootStatueBlock.FACING);
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180.0F));

        ItemStack renderItem = blockEntity.getLootItem();
        if (!renderItem.isEmpty()) {
            poseStack.pushPose();
            double[] doubleArray = new double[3];
            switch(blockEntity.getBlockState().getBlock().getRegistryName().toString()) {
                case "vaultadditions:loot_statue_vault" -> {
                    doubleArray[0] = 0.0;
                    doubleArray[1] = 0.1;
                    doubleArray[2] = 0.05;
                }
                case "vaultadditions:loot_statue_gift" -> {
                    doubleArray[0] = 0.0;
                    doubleArray[1] = 0.15;
                    doubleArray[2] = 0.2;
                }
                case "vaultadditions:loot_statue_gift_mega" -> {
                    doubleArray[0] = 0.0;
                    doubleArray[1] = 0.3;
                    doubleArray[2] = 0.1;
                }
                case "vaultadditions:loot_statue_arena" -> {
                    doubleArray[0] = 0.0;
                    doubleArray[1] = 0.0;
                    doubleArray[2] = 0.05;
                }
            }
            poseStack.translate(doubleArray[0], doubleArray[1], doubleArray[2]); // ;
            BakedModel bakedmodel = this.itemRenderer.getModel(renderItem, null, null, 0);
            float scale = bakedmodel.isGui3d() ? 0.5F : 0.28F;
            poseStack.scale(scale, scale, scale);
            this.itemRenderer.render(renderItem, ItemTransforms.TransformType.FIXED, false, poseStack, multiBufferSource, combinedLight, combinedOverlay, bakedmodel);

            poseStack.popPose();
        }

        String latestNickname = blockEntity.getSkin().getLatestNickname();
        if (!StringUtils.isEmpty(latestNickname)) {
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                if(mc.hitResult instanceof BlockHitResult result) {
                    if (blockEntity.getBlockPos().equals(result.getBlockPos().immutable())) {
                        MutableComponent text = (new TextComponent(latestNickname)).withStyle(ChatFormatting.WHITE);
                        if (blockEntity.isDead()) {
                            text = (new TextComponent("☠ ")).withStyle(ChatFormatting.RED).append(text);
                        }

                        this.renderLabel(blockEntity, poseStack, text);
                    }
                }
            }
        }

        this.drawPlayerModel(poseStack, multiBufferSource, blockEntity, combinedLight, combinedOverlay);
    }

    private void drawPlayerModel(PoseStack matrixStack, MultiBufferSource buffer, LootStatueBlockEntity blockEntity, int combinedLight, int combinedOverlay) {
        SkinProfile skin = blockEntity.getSkin();
        ResourceLocation skinLocation = StringUtil.isNullOrEmpty(skin.getLatestNickname()) ? STONE_SKIN : skin.getLocationSkin();
        RenderType renderType = PLAYER_MODEL.renderType(skinLocation);
        PLAYER_MODEL.young = false;
        PLAYER_MODEL.leftArm.xRot = -120.0F;
        PLAYER_MODEL.leftSleeve.xRot = -120.0F;
        PLAYER_MODEL.rightArm.xRot = -120.0F;
        PLAYER_MODEL.rightSleeve.xRot = -120.0F;
        PLAYER_MODEL.head.zRot = 0.0F;
        PLAYER_MODEL.hat.zRot = 0.0F;
        PLAYER_MODEL.leftLeg.zRot = 0.0F;
        PLAYER_MODEL.leftLeg.xRot = 0.0F;
        PLAYER_MODEL.leftLeg.yRot = 0.0F;
        PLAYER_MODEL.rightLeg.zRot = 0.0F;
        PLAYER_MODEL.rightLeg.xRot = 0.0F;
        PLAYER_MODEL.rightLeg.yRot = 0.0F;

        PLAYER_MODEL.setSlim(skin.isSlim());

        VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
        float scale = 0.5F;
        float statueOffset = -0.15F;
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, statueOffset);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        matrixStack.scale(scale, scale, scale);

        double[] doubleArray = new double[3];
        switch(blockEntity.getBlockState().getBlock().getRegistryName().toString()) {
            case "vaultadditions:loot_statue_vault"-> {
                doubleArray[0] = 0.0;
                doubleArray[1] = -0.75;
                doubleArray[2] = 0.0;
            }
            case "vaultadditions:loot_statue_gift" -> {
                doubleArray[0] = 0.0;
                doubleArray[1] = -0.8;
                doubleArray[2] = -0.3;
            }
            case "vaultadditions:loot_statue_gift_mega" -> {
                doubleArray[0] = 0.0;
                doubleArray[1] = -1.0;
                doubleArray[2] = 0.0;
                PLAYER_MODEL.leftLeg.xRot = 55.0F;
                PLAYER_MODEL.rightLeg.xRot = 55.0F;
                PLAYER_MODEL.leftLeg.yRot = 24.8F;
                PLAYER_MODEL.rightLeg.yRot = -24.8F;
                PLAYER_MODEL.rightArm.xRot = -120.4F;
                PLAYER_MODEL.rightSleeve.xRot = -120.4F;
                PLAYER_MODEL.leftArm.xRot = -120.4F;
                PLAYER_MODEL.leftSleeve.xRot = -120.4F;

            }
            case "vaultadditions:loot_statue_arena" -> {
                doubleArray[0] = 0.0;
                doubleArray[1] = -0.5;
                doubleArray[2] = 0.0;
            }
        }


        matrixStack.translate(doubleArray[0], doubleArray[1], doubleArray[2]);
        PLAYER_MODEL.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
        if (buffer instanceof MultiBufferSource.BufferSource) {
            ((MultiBufferSource.BufferSource)buffer).endBatch(renderType);
        }
    }

    private void renderLabel(LootStatueBlockEntity blockEntity, PoseStack poseStack, Component text) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        HitResult hitResult = minecraft.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
            if (!hitPos.equals(blockEntity.getBlockPos())) return;

            Direction direction = blockEntity.getBlockState().getValue(LootStatueBlock.FACING);
            float rotationY = switch (direction) {
                case NORTH -> 0.0F;
                case EAST -> 270.0F;
                case SOUTH -> 180.0F;
                case WEST -> 90.0F;
                default -> 0.0F; // Fallback for unexpected cases
            };

            // Render text and background above the block
            float offset = 0.0F;
            if(blockEntity.getBlockState().getBlock().getRegistryName().toString().equals("vaultadditions:loot_statue_gift_mega")) offset = 0.1F;
            poseStack.pushPose();
            poseStack.translate(0.0, 0.85 + offset, -0.15); // Adjust position above the block
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-rotationY));
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(-0.025F, -0.025F, 0.025F); // Scale for text rendering

            // Slightly offset in Z-direction to prevent Z-fighting
            poseStack.translate(0, 0, -0.01);

            Font font = minecraft.font;



            float textWidth = font.width(text);

            float backgroundPadding = 4; // Padding around the text
            float backgroundWidth = textWidth + backgroundPadding * 2;
            float backgroundHeight = 10; // Adjust height based on text size

            // Render background box
            RenderSystem.enableBlend(); // Enable transparency
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest(); // Ensure it renders above everything
            GuiComponent.fill(
                    poseStack,
                    (int) -backgroundWidth / 2,
                    (int) -backgroundHeight / 2,
                    (int) backgroundWidth / 2,
                    (int) backgroundHeight / 2,
                    1711276032 // Semi-transparent black
            );

            // Render the "Filled:" text in white
            font.draw(poseStack, text, -textWidth / 2, -4, 0xFFFFFF);
            // Restore render settings
            RenderSystem.enableDepthTest(); // Re-enable depth test for other elements
            RenderSystem.disableBlend(); // Disable transparency blending

            // Pop pose stack
            poseStack.popPose();
        }
    }


}
