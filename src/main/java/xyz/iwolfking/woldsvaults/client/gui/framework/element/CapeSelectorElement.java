package xyz.iwolfking.woldsvaults.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ScrollableListSelectorElement;
import iskallia.vault.client.gui.framework.element.SelectableButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CapeSelectorElement extends ScrollableListSelectorElement<CapeSelectorElement, CapeSelectorElement.CapeEntry, CapeSelectorElement.CapeSelectableElement> {

    private final CapeSelectorModel model;

    public CapeSelectorElement(ISpatial spatial, List<CapeEntry> entries, Consumer<CapeEntry> onSelect) {
        super(spatial, new CapeSelectorModel(entries, onSelect));
        this.model = (CapeSelectorModel) this.selectorModel;
    }

    public void setEntries(List<CapeEntry> newEntries) {
        this.model.setEntries(newEntries);
        this.refreshElements();
    }

    public static class CapeSelectorModel extends SelectorModel<CapeSelectableElement, CapeEntry> {

        private List<CapeEntry> entries;
        private final Consumer<CapeEntry> onSelectCallback;

        public CapeSelectorModel(List<CapeEntry> entries, Consumer<CapeEntry> onSelectCallback) {
            this.entries = new java.util.ArrayList<>(entries);
            this.onSelectCallback = onSelectCallback;
            this.whenSelected(elem -> onSelectCallback.accept(elem.getEntry()));
        }

        public void setEntries(List<CapeEntry> newEntries) {
            this.entries = new java.util.ArrayList<>(newEntries);
        }

        @Override
        public List<CapeEntry> getEntries() {
            return entries;
        }

        @Override
        public CapeSelectableElement createSelectable(ISpatial spatial, CapeEntry entry) {
            return new CapeSelectableElement(spatial, entry);
        }
    }

    public static class CapeSelectableElement extends SelectableButtonElement<CapeSelectableElement> {

        private final CapeEntry entry;
        private final LabelTextStyle textStyle;

        public CapeSelectableElement(ISpatial spatial, CapeEntry entry) {
            super(Spatials.positionXY(spatial.x(), spatial.y()).size(spatial.width(), 16), ScreenTextures.BUTTON_ALCHEMY_MODIFIER_TEXTURES, () -> {});
            this.entry = entry;
            this.textStyle = LabelTextStyle.defaultStyle().shadowFromTextColor().build();
            Component tooltipText = entry.buildTooltip();
            if (tooltipText != null) {
                this.tooltip(() -> tooltipText);
            }
        }

        public CapeEntry getEntry() {
            return entry;
        }

        @Override
        public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            super.render(renderer, poseStack, mouseX, mouseY, partialTick);

            poseStack.pushPose();
            poseStack.translate(0, 0, 1);

            Component displayText = entry.getDisplayComponent();
            this.textStyle.textBorder().render(renderer,
                    poseStack,
                    displayText,
                    TextWrap.wrap(),
                    TextAlign.LEFT,
                    this.worldSpatial.x() + 4,
                    this.worldSpatial.y() + 4,
                    this.worldSpatial.z(),
                    this.worldSpatial.width() - 8);
            poseStack.popPose();
        }
    }

    public record CapeEntry(@Nullable String id, String displayName, String description, boolean discovered, @Nullable Component requirementText) {
        public CapeEntry(String id, String displayName, String description, boolean discovered) {
            this(id, displayName, description, discovered, null);
        }

        public Component getDisplayComponent() {
            if (id == null) {
                return new TextComponent("None").withStyle(ChatFormatting.GRAY);
            }
            if (!discovered) {
                return new TextComponent(displayName)
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.STRIKETHROUGH);
            }
            return new TextComponent(displayName).withStyle(ChatFormatting.WHITE);
        }

        @Nullable
        public Component buildTooltip() {
            if (id == null) return null;
            if (!discovered) {
                return requirementText;
            }
            if (description != null && !description.isEmpty()) {
                return new TextComponent(description).withStyle(ChatFormatting.GRAY);
            }
            return null;
        }
    }
}
