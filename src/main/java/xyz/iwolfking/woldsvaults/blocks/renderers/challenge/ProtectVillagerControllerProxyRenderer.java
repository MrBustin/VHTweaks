package xyz.iwolfking.woldsvaults.blocks.renderers.challenge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.task.renderer.context.RendererContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerProxyBlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtectVillagerControllerProxyRenderer implements BlockEntityRenderer<ProtectVillagerControllerProxyBlockEntity> {

    private final Font font;

    public ProtectVillagerControllerProxyRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(ProtectVillagerControllerProxyBlockEntity entity, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        RenderSystem.enableDepthTest();
        matrixStack.pushPose();
        float scale = 0.02F;
        matrixStack.translate(0.5D, 2.5D, 0.5D);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.translate(-65.0D, -11.0D, 0.0D);

        RendererContext context = new RendererContext(matrixStack, partialTick, MultiBufferSource.immediate(Tesselator.getInstance().getBuilder()), this.font);
        List<Component> lines = new ArrayList<>();

        if (!entity.getLabel().isEmpty()) {
            lines.add(new TextComponent(entity.getLabel()).withStyle(ChatFormatting.GOLD));
        }

        for(ChallengeAction<?> action : entity.getActions()) {
            lines.add(action.getText());
        }

        Collections.reverse(lines);

        for(Component line : lines) {
            Component shadow = new TextComponent("").append(line.getString()).withStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
            context.renderText(shadow, 66.0F, 66.0F, true, true);
            context.renderText(line, 65.0F, 65.0F, true, true);
            context.translate(0.0D, -11.0D, 0.0D);
        }

        matrixStack.popPose();
    }

}
