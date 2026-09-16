package xyz.iwolfking.woldsvaults.container.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.*;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.bounty.element.HeaderElement;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.gear.VaultEtchingConfig;
import iskallia.vault.config.greed.GreedChallengeEntry;
import iskallia.vault.config.greed.GreedQuestEntry;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.VaultGearType;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.etching.EtchingHelper;
import iskallia.vault.greed.GreedChallengeSlot;
import iskallia.vault.greed.GreedQuestSlot;
import iskallia.vault.init.*;
import iskallia.vault.item.DeckSocketItem;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.modifiers.CrystalModifiers;
import iskallia.vault.item.crystal.properties.CrystalProperties;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.gear.EtchingItem;
import iskallia.vault.network.message.*;
import iskallia.vault.task.Task;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import iskallia.vault.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import xyz.iwolfking.woldsvaults.container.CrystalTraderContainer;
import xyz.iwolfking.woldsvaults.data.PlayerCrystalTraderData;
import xyz.iwolfking.woldsvaults.network.ServerboundCrystalTraderBuyMessage;
import xyz.iwolfking.woldsvaults.network.ServerboundCrystalTraderResetMessage;
import xyz.iwolfking.woldsvaults.util.CrystalTraderCurrencyHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class CrystalTraderScreen extends AbstractElementContainerScreen<CrystalTraderContainer> {
    protected final Inventory playerInventory;
    protected final List<IElement> contentElements = new ArrayList();
    protected final List<IElement> detailElements = new ArrayList();
    private boolean suppressContainerClose = false;

    protected CrystalTraderScreen(CrystalTraderContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ScreenRenderers.getImmediate(), ScreenTooltipRenderer::create);
        this.playerInventory = inventory;
        this.setGuiSize(Spatials.size(305, 199));
    }

    protected abstract CrystalTraderScreen.Tab getTab();

    protected abstract String getTabTitle();

    protected abstract void buildContent();

    protected void init() {
        super.init();
        this.removeAllElements();
        this.contentElements.clear();
        this.detailElements.clear();
        this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(0, 0).size(147, 189), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout(this.translateWorldSpatial()));
        this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(148, 0).size(147, 189), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout(this.translateWorldSpatial()));
        this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(7, 7), (new TextComponent(this.getTabTitle())).withStyle(Style.EMPTY.withColor(-11184811)), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
        this.buildContent();
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
    }


    public void removed() {
        if (!this.suppressContainerClose) {
            super.removed();
        }

    }

    protected <E extends IElement> E addContentEl(E el) {
        this.contentElements.add(el);
        return (E)this.addElement(el);
    }

    protected <E extends IElement> E addDetailEl(E el) {
        this.detailElements.add(el);
        return (E)this.addElement(el);
    }

    protected void clearContent() {
        this.contentElements.forEach((x$0) -> this.removeElement(x$0));
        this.contentElements.clear();
    }

    protected void clearDetail() {
        this.detailElements.forEach((x$0) -> this.removeElement(x$0));
        this.detailElements.clear();
    }

    protected void playClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected void addShopRestockButton() {
        int iconW = ScreenTextures.CYCLE.width();
        int btnX = 147 - iconW - 7;
        this.addContentEl((ButtonElement)((ButtonElement)(new ButtonElement(Spatials.positionXY(btnX, 5), ScreenTextures.CYCLE_BUTTONS, () -> {
            xyz.iwolfking.woldsvaults.init.additions.ModNetwork.sendToServer(new ServerboundCrystalTraderResetMessage());
            this.playClick();
        })).tooltip(Tooltips.single(() -> (new TextComponent("Restock (" + ((CrystalTraderContainer)this.menu).getResetCost() + " Reputation)")).withStyle(((CrystalTraderContainer)this.menu).getReputationBalance() >= ((CrystalTraderContainer)this.menu).getResetCost() ? Style.EMPTY.withColor(-1) : Style.EMPTY.withColor(-43691))))).setDisabled(() -> ((CrystalTraderContainer)this.menu).getReputationBalance() < ((CrystalTraderContainer)this.menu).getResetCost()).layout(this.translateWorldSpatial()));
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (keyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.onClose();
            return true;
        }
    }

    protected static enum Tab {
        SHOP;
        private Tab() {
        }
    }

    protected static class ScrollList extends VerticalScrollClipContainer<CrystalTraderScreen.ScrollList> {
        public ScrollList(ISpatial spatial) {
            super(spatial, Padding.of(0, -1, 1, 1), (NineSlice.TextureRegion)null, ScreenTextures.SCROLLBAR_HANDLE_SLIM, ScreenTextures.SCROLLBAR_HANDLE_SLIM_DISABLED, ScreenTextures.INSET_GREY_BACKGROUND);
        }

        public float getScrollValue() {
            return this.verticalScrollBarElement.getValue();
        }

        public void setScrollValue(float value) {
            this.verticalScrollBarElement.setValue(value);
        }

        public <T extends IElement> T addElement(T element) {
            return (T)super.addElement(element);
        }
    }

    public static class Shop extends CrystalTraderScreen {
        private int selectedOffer = -1;
        private int lastOfferHash = 0;
        private int lastCoinCount = -1;
        private float savedScrollValue = 0.0F;
        private ScrollList shopScrollList;

        public Shop(CrystalTraderContainer container, Inventory inventory, Component title) {
            super(container, inventory, title);
        }

        protected Tab getTab() {
            return CrystalTraderScreen.Tab.SHOP;
        }

        protected String getTabTitle() {
            return "Shop";
        }

        protected void buildContent() {
            this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(7, 17).size(133, 165), ScreenTextures.INSET_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
            List<PlayerCrystalTraderData.TradeOffer> offers = (this.menu).getOffers();
            this.shopScrollList = this.addContentEl((new ScrollList(Spatials.positionXY(7, 17).size(133, 165))).layout(this.translateWorldSpatial()));
            this.shopScrollList.setScrollValue(this.savedScrollValue);
            ScrollList scrollList = this.shopScrollList;
            int contentW = 125;
            int cardW = contentW / 2;
            if (offers.isEmpty()) {
                scrollList.addElement(new LabelElement(Spatials.positionXY(4, 4), Spatials.size(contentW - 8, 20), (new TextComponent("No trades available")).withStyle(ChatFormatting.GRAY), LabelTextStyle.defaultStyle().center()));
            } else {
                for(int i = 0; i < offers.size(); ++i) {
                    final int offerIndex = i;
                    PlayerCrystalTraderData.TradeOffer offer = offers.get(i);
                    int col = i % 2;
                    int row = i / 2;
                    int x = col * cardW + 1;
                    int y = row * 38;
                    ItemStack displayStack = offer.createItemStack();
                    boolean soldOut = (this.menu).isSoldOut(i);
                    NineSliceButtonElement<?> card = new NineSliceButtonElement(Spatials.positionXY(x, y).size(cardW, 38), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> {
                        if (!(this.menu).isSoldOut(offerIndex)) {
                            this.selectOffer(offerIndex);
                        }

                    });
                    card.setDisabled(soldOut);
                    if (!soldOut && !displayStack.isEmpty()) {
                        scrollList.addElement(new ItemStackDisplayElement(Spatials.positionXYZ(x + (cardW - 16) / 2, y + 11, 10), displayStack));
                    }

                    if (this.selectedOffer == i && !soldOut) {
                        scrollList.addElement(new NineSliceElement(Spatials.positionXYZ(x + 2, y + 2, 15).size(cardW - 4, 34), ScreenTextures.ENCHANTING_TABLE_BUTTON_CORNERS));
                    }

                    scrollList.addElement(card);
                }
            }

            this.addShopRestockButton();
            this.updateDetail();
        }

        private void selectOffer(int index) {
            this.selectedOffer = index;
            if (this.shopScrollList != null) {
                this.savedScrollValue = this.shopScrollList.getScrollValue();
            }

            this.clearContent();
            this.clearDetail();
            this.buildContent();
            this.requestLayout();
        }

        private void updateDetail() {
            List<PlayerCrystalTraderData.TradeOffer> offers = ((CrystalTraderContainer)this.menu).getOffers();
            if (this.selectedOffer >= 0 && this.selectedOffer < offers.size() && !((CrystalTraderContainer)this.menu).isSoldOut(this.selectedOffer)) {
                PlayerCrystalTraderData.TradeOffer offer = offers.get(this.selectedOffer);
                ItemStack displayStack = offer.createItemStack();
                boolean canAfford = ((CrystalTraderContainer)this.menu).getPlayerCoinCount() >= offer.coinCost();
                this.addDetailEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(155, 7).size(133, 152), ScreenTextures.INSET_DARK_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
                ItemStackDisplayElement<?> itemDisplay = new ItemStackDisplayElement(Spatials.positionXY(209, 10).size(24, 24), displayStack);
                itemDisplay.setScale(1.5F);
                itemDisplay.tooltip(Tooltips.multi(() -> displayStack.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)));
                this.addDetailEl((ItemStackDisplayElement)itemDisplay.layout(this.translateWorldSpatial()));
                this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXYZ(159, 38, 1), Spatials.size(125, 10), displayStack.getHoverName().copy().withStyle(Style.EMPTY.withColor(-1)), LabelTextStyle.defaultStyle().center().wrap().shadowFromTextColor())).layout(this.translateWorldSpatial()));
                this.buildItemInfo(displayStack);
                int idx = this.selectedOffer;
                int buyBtnX = 155;
                int buyBtnY = 162;
                int buyBtnW = 133;
                int buyBtnH = 20;
                NineSliceButtonElement<?> buyBtn = (NineSliceButtonElement)this.addDetailEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(buyBtnX, buyBtnY).size(buyBtnW, buyBtnH), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> this.completePurchase(idx))).layout(this.translateWorldSpatial()));
                buyBtn.setDisabled(!canAfford);
                int cost = offer.coinCost();
                String buyText = "Buy x" + cost;
                int textWidth = Minecraft.getInstance().font.width(buyText);
                int iconSize = 9;
                int iconGap = 2;
                int totalWidth = textWidth + iconGap + iconSize;
                int startX = buyBtnX + (buyBtnW - totalWidth) / 2;
                int textColor = canAfford ? -1 : -7829368;
                this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXYZ(startX, buyBtnY + 1 + (buyBtnH - 9) / 2, 2), (new TextComponent(buyText)).withStyle(Style.EMPTY.withColor(textColor)), LabelTextStyle.shadowFromTextColor())).layout(this.translateWorldSpatial()));
                ItemStackDisplayElement<?> coinIcon = new ItemStackDisplayElement(Spatials.positionXYZ(startX + textWidth + iconGap, buyBtnY + ((buyBtnH - iconSize) / 2) - 5, 10), CrystalTraderCurrencyHelper.createCurrencyStack());
                coinIcon.setScale((float)iconSize / 8.0F);
                this.addDetailEl((ItemStackDisplayElement)coinIcon.layout(this.translateWorldSpatial()));
            } else {
                this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXY(148, 94), Spatials.size(147, 10), (new TextComponent("Select an item")).withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle().center().wrap())).layout(this.translateWorldSpatial()));
            }
        }

        private void buildItemInfo(ItemStack displayStack) {
            int infoX = 158;
            int infoY = 60;
            int infoW = 130;
            int infoH = 99;
            List<Component> lines = new ArrayList();
            if (displayStack.getItem() instanceof InfusedCatalystItem) {
                if (!InfusedCatalystItem.isSuper(displayStack)) {
                    int size = (Integer)InfusedCatalystItem.getSize(displayStack).orElse(0);
                    lines.add((new TextComponent("Size: ")).append((new TextComponent(String.valueOf(size))).withStyle(ChatFormatting.GRAY)));
                }

                for(ResourceLocation modifierId : InfusedCatalystItem.getModifiers(displayStack)) {
                    VaultModifierRegistry.getOpt(modifierId).ifPresent((modifier) -> lines.add((new TextComponent("• " + modifier.getDisplayName())).withStyle(Style.EMPTY.withColor(modifier.getDisplayTextColor()))));
                }
            } else if (displayStack.getItem() == ModItems.INSCRIPTION) {
                InscriptionData data = InscriptionData.from(displayStack);
                lines.add((new TextComponent("Time added: ")).append((new TextComponent(UIHelper.formatTimeString((long)data.getTime()))).withStyle(Style.EMPTY.withColor(4700405))));

                for(InscriptionData.Entry entry : data.getEntries()) {
                    String roomStr = entry.count > 1 ? "Rooms" : "Room";
                    lines.add((new TextComponent("• ")).withStyle(ChatFormatting.GRAY).append((new TextComponent(String.valueOf(entry.count))).withStyle(ChatFormatting.GRAY)).append(" ").append(entry.toRoomEntry().getName()).append((new TextComponent(" " + roomStr)).withStyle(ChatFormatting.GRAY)));
                }

                List<ResourceLocation> modifiers = data.getModifiers();
                if (modifiers != null) {
                    for(ResourceLocation modifierId : modifiers) {
                        VaultModifierRegistry.getOpt(modifierId).ifPresent((modifier) -> {
                            TextColor textColor = modifier.getDisplayTextColor();
                            lines.add((new TextComponent("• " + modifier.getDisplayName())).withStyle(textColor != null ? Style.EMPTY.withColor(textColor) : Style.EMPTY));
                        });
                    }
                }
            } else if (displayStack.getItem() instanceof EtchingItem) {
                VaultGearData gearData = VaultGearData.read(displayStack);
                if (gearData.getState() == VaultGearState.IDENTIFIED && ModConfigs.isInitialized()) {
                    ResourceLocation etchingId = (ResourceLocation)gearData.getAttributes(ModGearAttributes.ETCHING).findFirst().map(VaultGearAttributeInstance::getValue).orElse(null);
                    if (etchingId != null) {
                        VaultEtchingConfig.EtchingEntry entry = ModConfigs.ETCHINGS.getEtchingConfig(etchingId);
                        if (entry != null) {
                            MutableComponent text = (new TextComponent("Type: ")).withStyle(ChatFormatting.GRAY).append((new TextComponent(String.join(", ", entry.getTypeGroups()))).withStyle(ChatFormatting.WHITE));
                            List<String> groupTypes = new ArrayList();
                            if (Screen.hasShiftDown()) {
                                for(String type : entry.getTypeGroups()) {
                                    groupTypes.addAll(ModConfigs.ETCHINGS.getGroup(type).stream().map(VaultGearType::getDisplayName).toList());
                                }
                            }

                            if (!groupTypes.isEmpty()) {
                                text.append((new TextComponent(" (" + String.join(", ", groupTypes) + ")")).withStyle(ChatFormatting.GRAY));
                            }

                            lines.add(text);
                            lines.add(TextUtil.applyColorTags(EtchingHelper.formatDescription(entry.getDescription(), EtchingHelper.getEtching(displayStack))));
                        }
                    }
                }
            } else if (displayStack.getItem() instanceof VaultCrystalItem) {
                CrystalData data = CrystalData.read(displayStack);
                CrystalModifiers modifiers = data.getModifiers();
                CrystalProperties properties = data.getProperties();

                for(VaultModifierStack stack : modifiers.getList()) {
                    lines.add(stack.getModifier().getChatDisplayNameComponent(stack.getSize()).copy().withStyle((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(stack.getModifier().getDisplayDescriptionFormatted(stack.getSize()))))));
                }

                data.getHazards().addText(lines, lines.size(), TooltipFlag.Default.NORMAL, (float)ClientScheduler.INSTANCE.getTick(), (Integer)properties.getLevel().orElse(0));
            } else if (displayStack.getItem() instanceof DeckSocketItem) {
                DeckSocketItem.getDeckModifier(displayStack).ifPresent((socket) -> socket.addText(lines, 0, TooltipFlag.Default.NORMAL, (float)ClientScheduler.INSTANCE.getTick()));
            }

            if (!lines.isEmpty()) {
                ScrollList infoScroll = (ScrollList)this.addDetailEl((ScrollList)(new ScrollList(Spatials.positionXY(infoX, infoY).size(infoW, infoH))).layout(this.translateWorldSpatial()));
                int innerW = infoW - 7;
                int labelW = innerW - 4;
                int yOff = 2;

                for(Component line : lines) {
                    int lineCount = Minecraft.getInstance().font.split(line, labelW).size();
                    int lineHeight = Math.max(1, lineCount) * 9;
                    infoScroll.addElement(new LabelElement(Spatials.positionXY(2, yOff), Spatials.size(labelW, lineHeight), line, LabelTextStyle.defaultStyle().wrap()));
                    yOff += lineHeight + 3;
                }
            }

        }

        private void completePurchase(int tradeIndex) {
            List<PlayerCrystalTraderData.TradeOffer> offers = ((CrystalTraderContainer)this.menu).getOffers();
            if (tradeIndex >= 0 && tradeIndex < offers.size()) {
                if (!((CrystalTraderContainer)this.menu).isSoldOut(tradeIndex)) {
                    if (((CrystalTraderContainer)this.menu).getPlayerCoinCount() >= offers.get(tradeIndex).coinCost()) {
                        xyz.iwolfking.woldsvaults.init.additions.ModNetwork.sendToServer(new ServerboundCrystalTraderBuyMessage(tradeIndex));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2F));
                        ((CrystalTraderContainer)this.menu).getSoldOutIndices().add(tradeIndex);
                        this.selectedOffer = -1;
                        this.clearContent();
                        this.clearDetail();
                        this.buildContent();
                        this.requestLayout();
                    }
                }
            }
        }

        protected void containerTick() {
            super.containerTick();
            int hash = ((CrystalTraderContainer)this.menu).getOffers().hashCode();
            int coins = ((CrystalTraderContainer)this.menu).getPlayerCoinCount();
            if (hash != this.lastOfferHash || coins != this.lastCoinCount) {
                this.lastOfferHash = hash;
                this.lastCoinCount = coins;
                this.selectedOffer = -1;
                this.clearContent();
                this.clearDetail();
                this.buildContent();
                this.requestLayout();
            }

        }
    }

}
