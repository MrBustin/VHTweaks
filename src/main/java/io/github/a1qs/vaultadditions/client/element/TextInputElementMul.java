package io.github.a1qs.vaultadditions.client.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextInputElementMul<E extends TextInputElementMul<E>> extends AbstractSpatialElement<E> implements IRenderedElement, IGuiEventElement {

    protected boolean visible;
    private final EditBox editBox;

    private Consumer<String> onChange = str -> {};

    public TextInputElementMul(ISpatial spatial, Font font) {
        super(spatial);
        this.editBox = new EditBox(font, spatial.x(), spatial.y(), spatial.width(), spatial.height(), TextComponent.EMPTY);
        this.setVisible(true);
    }

    public <T extends TextInputElementMul<E>> T adjustEditBox(Consumer<EditBox> fn) {
        fn.accept(this.editBox);
        return (T) this;
    }

    public void setMaxLength(int length) {
        this.editBox.setMaxLength(length);
    }

    @Override
    protected void layoutSelf(ISize screen, ISpatial gui, ISpatial parent) {
        super.layoutSelf(screen, gui, parent);
        this.editBox.x = this.worldSpatial.x();
        this.editBox.y = this.worldSpatial.y();
    }



    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        // We dont consume the event, we simply ball. (I hope this is fine)
        this.editBox.setFocus(this.isMouseOver(mouseX, mouseY));
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
        this.editBox.setFocus(false);
        return IGuiEventElement.super.mouseClicked(mouseX, mouseY, buttonIndex);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.editBox.setVisible(visible);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public boolean isFocused() {
        return this.editBox.isFocused();
    }

    public String getInput() {
        return this.editBox.getValue();
    }

    public void setInput(String input) {
        this.editBox.setValue(input);
    }

    public void onTextChanged(Consumer<String> changeFn) {
        this.onChange = this.onChange.andThen(changeFn);
        this.editBox.setResponder(this.onChange);
    }

    public void tickEditBox() {
        this.editBox.tick();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.canConsumeInput() && this.editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return this.editBox.canConsumeInput() && this.editBox.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(0,0,10);
        this.editBox.renderButton(poseStack, mouseX, mouseY, partialTick);
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }
}
