package xyz.iwolfking.woldsvaults.blocks.containers;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.ProgressElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import xyz.iwolfking.woldsvaults.blocks.tiles.VaultSalvagerTileEntity;

import java.util.Objects;

public class VaultSalvagerScreen  extends AbstractElementContainerScreen<VaultSalvagerContainer> {
    private final Inventory playerInventory;

    public VaultSalvagerScreen(VaultSalvagerContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
        this.playerInventory = inventory;
        this.setGuiSize(Spatials.size(176, 165));
        this.addElement((NineSliceElement)(new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
            world.translateXY(gui).size(Spatials.copy(gui));
        }));
        this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(8, 7), ((VaultSalvagerContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
            world.translateXY(gui);
        }));
        MutableComponent inventoryName = inventory.getDisplayName().copy();
        inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
        this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(8, 71), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
            world.translateXY(gui);
        }));
        this.addElement((SlotsElement)(new SlotsElement(this)).layout((screen, gui, parent, world) -> {
            world.positionXY(gui);
        }));
        IMutableSpatial var10003 = Spatials.positionXY(16, 50);
        ProgressElement.ProgressTextures var10004 = ScreenTextures.PROGRESS_ARROW;
        VaultSalvagerTileEntity var10005 = ((VaultSalvagerContainer)this.getMenu()).getTileEntity();
        Objects.requireNonNull(var10005);
        this.addElement((ProgressElement)(new ProgressElement(var10003, var10004, var10005::getProgressPercent)).layout((screen, gui, parent, world) -> {
            world.translateXY(gui);
        }));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {

    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key key = InputConstants.getKey(pKeyCode, pScanCode);
        if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        } else {
            this.onClose();
            return true;
        }
    }
}
