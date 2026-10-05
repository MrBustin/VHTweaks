package xyz.iwolfking.woldsvaults.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class PlayerPreviewElement<E extends PlayerPreviewElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {

    private boolean visible = true;
    private int scale;

    public PlayerPreviewElement(ISpatial spatial) {
        super(Spatials.positionXYZ(spatial.x(), spatial.y(), spatial.z()).size(spatial.width(), spatial.height()));
        this.scale = Math.max(20, spatial.height() / 2);
    }

    public E setScale(int scale) {
        this.scale = scale;
        return (E) this;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        LivingEntity entity = mc.player;
        if (entity == null) return;

        int centerX = this.worldSpatial.x() + this.worldSpatial.width() / 2;
        int centerY = this.worldSpatial.y() + this.worldSpatial.height() - 4;

        float deltaX = centerX - mouseX;
        float deltaY = centerY - this.scale - mouseY;

        renderEntityFromBehind(centerX, centerY, this.scale, deltaX, deltaY, entity);
    }

    private static void renderEntityFromBehind(int posX, int posY, int renderScale, float mouseDeltaX, float mouseDeltaY, LivingEntity entity) {
        float yawDrift = (float) Math.atan(mouseDeltaX / 40.0F);
        float pitchDrift = (float) Math.atan(mouseDeltaY / 40.0F);

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(posX, posY, 1050.0D);
        modelViewStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();

        PoseStack entityPose = new PoseStack();
        entityPose.translate(0.0D, 0.0D, 1000.0D);
        entityPose.scale(renderScale, renderScale, renderScale);

        Quaternion zRotation = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion xRotation = Vector3f.XP.rotationDegrees(pitchDrift * 20.0F);
        zRotation.mul(xRotation);
        entityPose.mulPose(zRotation);

        float origBodyRot = entity.yBodyRot;
        float origYRot = entity.getYRot();
        float origXRot = entity.getXRot();
        float origHeadRotO = entity.yHeadRotO;
        float origHeadRot = entity.yHeadRot;

        float facingYaw = yawDrift * 20.0F;
        entity.yBodyRot = facingYaw;
        entity.setYRot(facingYaw);
        entity.setXRot(-pitchDrift * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        xRotation.conj();
        dispatcher.overrideCameraOrientation(xRotation);
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(
                entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, entityPose,
                Minecraft.getInstance().renderBuffers().bufferSource(), 0x00F000F0));
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        dispatcher.setRenderShadow(true);

        entity.yBodyRot = origBodyRot;
        entity.setYRot(origYRot);
        entity.setXRot(origXRot);
        entity.yHeadRotO = origHeadRotO;
        entity.yHeadRot = origHeadRot;

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }
}
