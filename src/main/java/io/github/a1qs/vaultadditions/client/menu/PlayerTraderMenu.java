package io.github.a1qs.vaultadditions.client.menu;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.client.element.TextInputElementMul;
import io.github.a1qs.vaultadditions.container.PlayerTraderContainer;
import io.github.a1qs.vaultadditions.container.slot.GhostSlot;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.UpdatePlayerTraderDataMessage;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.*;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class PlayerTraderMenu extends AbstractElementContainerScreen<PlayerTraderContainer> {
    private final TextInputElementMul<?> sellAmount;
    private final TextInputElementMul<?> traderName;

    public PlayerTraderMenu(PlayerTraderContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
        this.imageWidth = 176;
        this.imageHeight = 180;


        // Background Texture
        this.addElement(
                new NineSliceElement<>(
                        this.getGuiSpatial(),
                        ScreenTextures.DEFAULT_WINDOW_BACKGROUND
                ).layout((screen, gui, parent, world) -> world
                        .translateXY(gui)
                        .size(Spatials.copy(gui))
                )
        );

        // Menu Title
        this.addElement(
                new LabelElement<>(
                        Spatials.positionXY(5, 4),
                        ((MutableComponent)this.title).withStyle(Style.EMPTY.withColor(0xFF_3f3f3f)),
                        LabelTextStyle.defaultStyle()
                ).layout((screen, gui, parent, world) -> world.translateXY(gui))
        );

        // "Inventory" Title
        MutableComponent inventoryName = inventory.getDisplayName().copy();
        inventoryName.withStyle(Style.EMPTY.withColor(0xFF_3F3F3F));
        this.addElement(
                new LabelElement<>(
                        Spatials.positionXY(8, 88),
                        inventoryName,
                        LabelTextStyle.defaultStyle()
                ).layout((screen, gui, parent, world) -> world.translateXY(gui))
        );

        // Slots
        this.addElement(
                new SlotsElement<>(
                        Spatials.zero(),
                        this.getMenu().slots,
                        ScreenTextures.INSET_ITEM_SLOT_BACKGROUND
                ).layout((screen, gui, parent, world) -> world.positionXY(gui))
        );


        // Input element for itemCount.
        this.sellAmount = this.addElement(
                new TextInputElementMul<>(
                        Spatials.copy(Spatials.positionXY(140, 20)).size(26, 12),
                        Minecraft.getInstance().font
                ).layout((screen, gui, parent, world) -> world.translateXY(gui))
        ).adjustEditBox(editBox -> {
            editBox.setFilter(input -> {
                if (input.isEmpty()) return true;
                int parsedAmount;

                try {
                    parsedAmount = Integer.parseInt(input);
                } catch (NumberFormatException exc) {
                    return false;
                }

                if (parsedAmount <= 64) {
                    return true;
                }

                // If the input exceeds the cap, adjust it to the maximum value
                editBox.setValue("64");
                return false;
            });
            editBox.setMaxLength(3);
            editBox.setValue("1");
        });

        this.sellAmount.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.sellAmount.isVisible()) {
                return false;
            }
            Component cmp = new TextComponent("Items per sale");
            tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
            return true;
        });

        this.addElement(this.traderName = new TextInputElementMul<>(
                Spatials.positionXY(7, 16).size(100, 12),
                Minecraft.getInstance().font
        ).layout((screen, gui, parent, world) -> world.translateXY(gui))
        ).adjustEditBox(box -> {
            box.setCanLoseFocus(true);
            box.setMaxLength(16);
            box.setValue(this.getMenu().getBlockEntity().getTraderName());
        });


        this.traderName.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.traderName.isVisible()) {
                return false;
            }
            Component cmp = new TextComponent("Trader display name");
            tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
            return true;
        });
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void containerTick() {
        super.containerTick();


        this.sellAmount.tickEditBox();
        this.traderName.tickEditBox();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {


        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (this.sellAmount.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (this.traderName.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
            if (!this.sellAmount.isFocused() && !this.traderName.isFocused()) {
                this.onClose();
            }

            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.sellAmount.charTyped(codePoint, modifiers)) {
            return true;
        }
        if (this.traderName.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if(!this.traderName.getInput().equals(this.getMenu().getBlockEntity().getTraderName())) {
            ModNetwork.CHANNEL.sendToServer(new UpdatePlayerTraderDataMessage(this.getMenu().getPos(), this.traderName.getInput()));
        }

        super.onClose();
    }


    //TODO it works but its not great.
    @Override
    protected void renderSlotItems(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.renderSlotItems(poseStack, mouseX, mouseY, partialTick);

        for (int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);

            if(slot instanceof GhostSlot && slot.hasItem()) {
                RenderSystem.disableDepthTest();
                GuiComponent.fill(poseStack, slot.x + 125, slot.y + 34, slot.x + 141, slot.y + 50, 0x99FFFFF0);

                RenderSystem.enableDepthTest();

            }
        }

    }
}
