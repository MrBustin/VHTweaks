package xyz.iwolfking.woldsvaults.container.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.data.ClientGreedTreeData;
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
import iskallia.vault.world.data.PlayerGreedTraderData;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class CrystalTraderScreen extends AbstractElementContainerScreen<CrystalTraderContainer> {
    protected static final int LEFT_W = 147;
    protected static final int RIGHT_W = 147;
    protected static final int PANEL_H = 189;
    protected static final int BG_SLICE = 5;
    protected static final int GUI_W = 305;
    protected static final int GUI_H = 199;
    protected static final int LEFT_X = 5;
    protected static final int LEFT_Y = 5;
    protected static final int RIGHT_X = 153;
    protected static final int RIGHT_Y = 5;
    protected static final int TITLE_H = 14;
    protected static final int PAD = 4;
    protected static final int SHOP_CARD_H = 38;
    protected static final int QUEST_ITEM_H = 19;
    protected static final int ITEM_GAP = 4;
    protected static final int BUTTON_H = 20;
    protected static final int SCROLL_TRIM = 10;
    protected final Inventory playerInventory;
    protected final List<IElement> contentElements = new ArrayList();
    protected final List<IElement> detailElements = new ArrayList();
    private boolean suppressContainerClose = false;
    private final List<float[]> reputationParticles = new ArrayList();
    private long lastParticleTick = 0L;

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
        CrystalTraderScreen.Tab current = this.getTab();
        this.addElement(this.createTab(current == CrystalTraderScreen.Tab.SHOP, ScreenTextures.ICON_GREED_COIN, 5, () -> this.switchTab(CrystalTraderScreen.Tab.SHOP)));
        this.addElement(this.createTab(current == CrystalTraderScreen.Tab.QUESTS, ScreenTextures.ICON_QUEST, 35, () -> this.switchTab(CrystalTraderScreen.Tab.QUESTS)));
        this.addElement(this.createTab(current == CrystalTraderScreen.Tab.CHALLENGES, ScreenTextures.TAB_ICON_PORTAL_VAULT, 65, () -> this.switchTab(CrystalTraderScreen.Tab.CHALLENGES)));
        this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(7, 7), (new TextComponent(this.getTabTitle())).withStyle(Style.EMPTY.withColor(-11184811)), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
        this.buildContent();
    }

    private TabElement<?> createTab(boolean selected, TextureAtlasRegion icon, int y, Runnable onClick) {
        return (TabElement)(new TabElement(Spatials.positionXY(selected ? -28 : -24, y), new TextureAtlasElement(selected ? ScreenTextures.TAB_BACKGROUND_LEFT_SELECTED : ScreenTextures.TAB_BACKGROUND_LEFT), new TextureAtlasElement(Spatials.positionXY(selected ? 8 : 4, 6), icon), onClick)).layout(this.translateWorldSpatial());
    }

    private void switchTab(CrystalTraderScreen.Tab tab) {
        if (tab != this.getTab()) {
            this.suppressContainerClose = true;
            Minecraft mc = Minecraft.getInstance();
            Object var10001;
            switch (tab) {
                case SHOP -> var10001 = new CrystalTraderScreen.Shop((CrystalTraderContainer)this.menu, this.playerInventory, this.title);
                case QUESTS -> var10001 = new CrystalTraderScreen.Quests((CrystalTraderContainer)this.menu, this.playerInventory, this.title);
                case CHALLENGES -> var10001 = new CrystalTraderScreen.Challenges((CrystalTraderContainer)this.menu, this.playerInventory, this.title);
                default -> throw new IncompatibleClassChangeError();
            }

            mc.setScreen((Screen)var10001);
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderGreedReputation(poseStack, partialTick);
    }

    private void renderGreedReputation(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        int reputation = ClientGreedTreeData.getGreedReputation();
        long currentTick = ClientScheduler.INSTANCE.getTick();
        float time = (float)currentTick + partialTick;
        float shimmerPhase = time * 0.015F % 1.0F;
        float shimmer = (float)((double)0.5F + (double)0.5F * Math.sin((double)shimmerPhase * Math.PI * (double)2.0F));
        int r = 255;
        int g = (int)(210.0F + 30.0F * shimmer);
        int b = (int)(50.0F + 40.0F * shimmer);
        int shimmerColor = r << 16 | g << 8 | b;
        String repNumber = String.valueOf(reputation);
        Component numberComponent = (new TextComponent(repNumber)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)));
        Component labelComponent = (new TextComponent(" Greed Reputation")).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(shimmerColor)));
        int numberWidth = minecraft.font.width(numberComponent);
        int labelWidth = minecraft.font.width(labelComponent);
        int totalWidth = numberWidth + labelWidth;
        int guiRight = this.leftPos + 305 - 5;
        int x = guiRight - totalWidth;
        int y = this.topPos - 12;
        minecraft.font.drawShadow(poseStack, numberComponent, (float)x, (float)y, 16777215);
        minecraft.font.drawShadow(poseStack, labelComponent, (float)(x + numberWidth), (float)y, shimmerColor);
        if (currentTick != this.lastParticleTick) {
            this.lastParticleTick = currentTick;
            if (reputation > 0 && minecraft.level != null && minecraft.level.random.nextFloat() < 0.4F) {
                float px = (float)x + minecraft.level.random.nextFloat() * (float)numberWidth;
                float py = (float)(y + 8);
                float vx = (minecraft.level.random.nextFloat() - 0.5F) * 0.3F;
                float vy = 0.3F + minecraft.level.random.nextFloat() * 0.4F;
                float life = (float)(15 + minecraft.level.random.nextInt(10));
                this.reputationParticles.add(new float[]{px, py, vx, vy, life, life});
            }

            this.reputationParticles.removeIf((pxx) -> pxx[4] <= 0.0F);

            for(float[] p : this.reputationParticles) {
                p[0] += p[2];
                p[1] += p[3];
                p[3] += 0.02F;
                p[4]--;
            }
        }

        poseStack.pushPose();
        poseStack.translate((double)0.0F, (double)0.0F, (double)400.0F);

        for(float[] p : this.reputationParticles) {
            float alpha = Math.max(0.0F, p[4] / p[5]);
            int pr = (int)(255.0F * (0.9F + 0.1F * alpha));
            int pg = (int)(180.0F * alpha + 80.0F);
            int pb = (int)(40.0F * alpha);
            int color = (int)(alpha * 255.0F) << 24 | pr << 16 | pg << 8 | pb;
            int size = alpha > 0.5F ? 2 : 1;
            fill(poseStack, (int)p[0], (int)p[1], (int)p[0] + size, (int)p[1] + size, color);
        }

        poseStack.popPose();
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
            ModNetwork.sendToServer(new ServerboundGreedTraderResetMessage());
            this.playClick();
        })).tooltip(Tooltips.single(() -> (new TextComponent("Restock (" + ((CrystalTraderContainer)this.menu).getResetCost() + " Reputation)")).withStyle(((CrystalTraderContainer)this.menu).getReputationBalance() >= ((CrystalTraderContainer)this.menu).getResetCost() ? Style.EMPTY.withColor(-1) : Style.EMPTY.withColor(-43691))))).setDisabled(() -> ((CrystalTraderContainer)this.menu).getReputationBalance() < ((CrystalTraderContainer)this.menu).getResetCost()).layout(this.translateWorldSpatial()));
    }

    protected void addQuestRestockButton() {
        int iconW = ScreenTextures.CYCLE.width();
        int btnX = 147 - iconW - 7;
        this.addContentEl((ButtonElement)((ButtonElement)(new ButtonElement(Spatials.positionXY(btnX, 5), ScreenTextures.CYCLE_BUTTONS, () -> {
            ModNetwork.sendToServer(new ServerboundGreedQuestRerollMessage());
            this.playClick();
        })).tooltip(Tooltips.single(() -> (new TextComponent("Re-roll (" + ((CrystalTraderContainer)this.menu).getQuestRerollCost() + " Coins)")).withStyle(((CrystalTraderContainer)this.menu).getPlayerCoinCount() >= ((CrystalTraderContainer)this.menu).getQuestRerollCost() ? Style.EMPTY.withColor(-1) : Style.EMPTY.withColor(-43691))))).setDisabled(() -> ((CrystalTraderContainer)this.menu).getPlayerCoinCount() < ((CrystalTraderContainer)this.menu).getQuestRerollCost()).layout(this.translateWorldSpatial()));
    }

    protected TaskProgress extractTaskProgress(GreedQuestSlot slot) {
        Task task = slot.getTask();
        if (task == null) {
            return null;
        } else if (task instanceof IProgressTask) {
            IProgressTask p = (IProgressTask)task;
            return p.getProgress();
        } else {
            Iterator progressTasks = task.getSelfAndDescendants(IProgressTask.class).iterator();
            if (progressTasks.hasNext()) {
                IProgressTask progressTask = (IProgressTask)progressTasks.next();
                return progressTask.getProgress();
            } else {
                return null;
            }
        }
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
        SHOP,
        QUESTS,
        CHALLENGES;

        private Tab() {
        }
    }

    protected static class ClickableElement<E extends CrystalTraderScreen.ClickableElement<E>> extends ContainerElement<E> {
        private final Runnable onClick;

        public ClickableElement(ISpatial spatial, Runnable onClick) {
            super(spatial);
            this.onClick = onClick;
        }

        public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.onClick.run();
            return true;
        }

        public <T extends IElement> T addElement(T element) {
            return (T)super.addElement(element);
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
            List<PlayerGreedTraderData.TradeOffer> offers = ((CrystalTraderContainer)this.menu).getOffers();
            this.shopScrollList = (ScrollList)this.addContentEl((ScrollList)(new ScrollList(Spatials.positionXY(7, 17).size(133, 165))).layout(this.translateWorldSpatial()));
            this.shopScrollList.setScrollValue(this.savedScrollValue);
            ScrollList scrollList = this.shopScrollList;
            int contentW = 125;
            int cardW = contentW / 2;
            if (offers.isEmpty()) {
                scrollList.addElement(new LabelElement(Spatials.positionXY(4, 4), Spatials.size(contentW - 8, 20), (new TextComponent("No trades available")).withStyle(ChatFormatting.GRAY), LabelTextStyle.defaultStyle().center()));
            } else {
                for(int i = 0; i < offers.size(); ++i) {
                    final int offerIndex = i;
                    PlayerGreedTraderData.TradeOffer offer = (PlayerGreedTraderData.TradeOffer)offers.get(i);
                    int col = i % 2;
                    int row = i / 2;
                    int x = col * cardW + 1;
                    int y = row * 38;
                    ItemStack displayStack = offer.createItemStack();
                    boolean soldOut = ((CrystalTraderContainer)this.menu).isSoldOut(i);
                    NineSliceButtonElement<?> card = new NineSliceButtonElement(Spatials.positionXY(x, y).size(cardW, 38), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> {
                        if (!((CrystalTraderContainer)this.menu).isSoldOut(offerIndex)) {
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
            List<PlayerGreedTraderData.TradeOffer> offers = ((CrystalTraderContainer)this.menu).getOffers();
            if (this.selectedOffer >= 0 && this.selectedOffer < offers.size() && !((CrystalTraderContainer)this.menu).isSoldOut(this.selectedOffer)) {
                PlayerGreedTraderData.TradeOffer offer = (PlayerGreedTraderData.TradeOffer)offers.get(this.selectedOffer);
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
                int var10000 = offer.coinCost();
                String buyText = "Buy x" + var10000;
                int textWidth = Minecraft.getInstance().font.width(buyText);
                int iconSize = 9;
                int iconGap = 2;
                int totalWidth = textWidth + iconGap + iconSize;
                int startX = buyBtnX + (buyBtnW - totalWidth) / 2;
                int textColor = canAfford ? -1 : -7829368;
                this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXYZ(startX, buyBtnY + 1 + (buyBtnH - 9) / 2, 2), (new TextComponent(buyText)).withStyle(Style.EMPTY.withColor(textColor)), LabelTextStyle.shadowFromTextColor())).layout(this.translateWorldSpatial()));
                ItemStackDisplayElement<?> coinIcon = new ItemStackDisplayElement(Spatials.positionXYZ(startX + textWidth + iconGap, buyBtnY + (buyBtnH - iconSize) / 2, 10), new ItemStack(ModItems.GREED_COIN));
                coinIcon.setScale((float)iconSize / 16.0F);
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
            List<PlayerGreedTraderData.TradeOffer> offers = ((CrystalTraderContainer)this.menu).getOffers();
            if (tradeIndex >= 0 && tradeIndex < offers.size()) {
                if (!((CrystalTraderContainer)this.menu).isSoldOut(tradeIndex)) {
                    if (((CrystalTraderContainer)this.menu).getPlayerCoinCount() >= ((PlayerGreedTraderData.TradeOffer)offers.get(tradeIndex)).coinCost()) {
                        ModNetwork.sendToServer(new ServerboundGreedTraderBuyMessage(tradeIndex));
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

    public static class Quests extends CrystalTraderScreen {
        private static final int ACTIVE_SLOT_H = 19;
        private int selectedQuest = -1;
        private int lastQuestDataHash = 0;

        public Quests(CrystalTraderContainer container, Inventory inventory, Component title) {
            super(container, inventory, title);
        }

        protected Tab getTab() {
            return CrystalTraderScreen.Tab.QUESTS;
        }

        protected String getTabTitle() {
            return "Quests";
        }

        protected void init() {
            super.init();
            ModNetwork.sendToServer(new ServerboundGreedQuestActionMessage(iskallia.vault.network.message.ServerboundGreedQuestActionMessage.Action.POPULATE, 0));
        }

        protected void buildContent() {
            this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(7, 40).size(133, 142), ScreenTextures.INSET_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
            this.addQuestRestockButton();
            List<GreedQuestSlot> originalSlots = ClientGreedTreeData.getQuestSlots();
            GreedQuestSlot activeSlot = (GreedQuestSlot)originalSlots.stream().filter(GreedQuestSlot::isActive).findFirst().orElse(null);
            int leftInnerX = 7;
            int leftInnerY = 17;
            int leftInnerW = 133;
            this.addContentEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(leftInnerX, leftInnerY).size(leftInnerW, 19), ScreenTextures.INSET_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
            if (activeSlot != null && ModConfigs.GREED_TRADER != null) {
                this.addContentEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(leftInnerX + 1, leftInnerY + 1).size(leftInnerW - 2, 17), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> this.selectQuest(originalSlots.indexOf(activeSlot)))).layout(this.translateWorldSpatial()));
                GreedQuestEntry activeEntry = ModConfigs.GREED_TRADER.getQuestById(activeSlot.getQuestId());
                if (activeEntry != null) {
                    int activeOrigIdx = originalSlots.indexOf(activeSlot);
                    this.addContentEl((LabelElement)(new LabelElement(Spatials.positionXY(leftInnerX + 4, leftInnerY + 5), Spatials.size(leftInnerW - 8, 9), (new TextComponent(activeEntry.getDisplayName())).withStyle(Style.EMPTY.withColor(-11252677)), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
                    this.addContentEl((ClickableElement)(new ClickableElement(Spatials.positionXY(leftInnerX, leftInnerY).size(leftInnerW, 19), () -> this.selectQuest(activeOrigIdx))).layout(this.translateWorldSpatial()));
                }
            } else {
                this.addContentEl((LabelElement)(new LabelElement(Spatials.positionXY(leftInnerX + 4, leftInnerY + 5), Spatials.size(leftInnerW - 8, 9), (new TextComponent("No active quest")).withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
            }

            int listY = leftInnerY + 19 + 4;
            int listH = 182 - listY;
            ScrollList scrollList = (ScrollList)this.addContentEl((ScrollList)(new ScrollList(Spatials.positionXY(leftInnerX, listY).size(leftInnerW, listH))).layout(this.translateWorldSpatial()));
            int contentW = leftInnerW - 9;
            List<GreedQuestSlot> sorted = new ArrayList(originalSlots);
            sorted.sort(Comparator.comparingInt(GreedQuestSlot::getSortOrder));
            int yOffset = 0;

            for(GreedQuestSlot slot : sorted) {
                if (!slot.isEmpty() && !slot.isActive() && ModConfigs.GREED_TRADER != null) {
                    GreedQuestEntry questEntry = ModConfigs.GREED_TRADER.getQuestById(slot.getQuestId());
                    if (questEntry != null) {
                        int originalIndex = originalSlots.indexOf(slot);
                        ClickableElement<?> item = new ClickableElement(Spatials.positionXY(1, yOffset).size(contentW, 19), () -> this.selectQuest(originalIndex));
                        item.addElement(new NineSliceButtonElement(Spatials.positionXY(0, 0).size(contentW, 19), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> {
                        }));
                        TextureAtlasRegion suffix = null;
                        ChatFormatting suffixColor = ChatFormatting.WHITE;
                        if (slot.isCompleted()) {
                            suffix = TextureAtlasRegion.of(ModTextureAtlases.QUESTS, VaultMod.id("gui/quests/check"));
                        }

                        int labelW = contentW - 8 - (suffix == null ? 0 : 14);
                        item.addElement(new LabelElement(Spatials.positionXY(4, 5), Spatials.size(labelW, 9), (new TextComponent(questEntry.getDisplayName())).withStyle(Style.EMPTY.withColor(-11252677)), LabelTextStyle.defaultStyle()));
                        if (suffix != null) {
                            item.addElement(new TextureAtlasElement(Spatials.positionXY(contentW - 4 - 15, 1), suffix));
                        }

                        scrollList.addElement(item);
                        yOffset += 19;
                    }
                }
            }

            this.updateDetail();
        }

        private void selectQuest(int index) {
            this.selectedQuest = index;
            this.clearDetail();
            this.updateDetail();
            this.requestLayout();
        }

        private void updateDetail() {
            List<GreedQuestSlot> questSlots = ClientGreedTreeData.getQuestSlots();
            if (this.selectedQuest >= 0 && this.selectedQuest < questSlots.size() && ModConfigs.GREED_TRADER != null) {
                GreedQuestSlot slot = (GreedQuestSlot)questSlots.get(this.selectedQuest);
                GreedQuestEntry questEntry = ModConfigs.GREED_TRADER.getQuestById(slot.getQuestId());
                if (questEntry != null) {
                    int rX = 152;
                    int rW = 139;
                    int btnH = 20;
                    this.addDetailEl((HeaderElement)(new HeaderElement(Spatials.positionXY(rX, 4).width(rW).height(20), (new TextComponent(questEntry.getDisplayName())).withStyle(ChatFormatting.WHITE))).layout(this.translateWorldSpatial()));
                    int detailY = 28;
                    int detailH = 189 - detailY - btnH - 3 - 3 - 3;
                    ScrollList detailScroll = (ScrollList)this.addDetailEl((ScrollList)(new ScrollList(Spatials.positionXY(rX, detailY).size(rW - 1, detailH))).layout(this.translateWorldSpatial()));
                    int innerW = rW - 7;
                    int yOff = 4;
                    String desc = questEntry.getDescription();
                    if (desc != null && !desc.isEmpty()) {
                        desc = desc.replace("%target", String.valueOf(slot.getRolledTarget()));
                        TextComponent descComponent = new TextComponent(desc);
                        int descWidth = innerW - 8;
                        int lineCount = Minecraft.getInstance().font.split(descComponent, descWidth).size();
                        int descHeight = lineCount * 9;
                        detailScroll.addElement(new LabelElement(Spatials.positionXY(4, yOff), Spatials.size(descWidth, descHeight), descComponent.withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle().wrap()));
                        yOff += descHeight + 4;
                    }

                    yOff += 12;
                    detailScroll.addElement(new LabelElement(Spatials.positionXYZ(4, yOff, 1), (new TextComponent("Rewards")).withStyle(ChatFormatting.WHITE), LabelTextStyle.defaultStyle().shadowFromTextColor()));
                    yOff += 12;
                    int displayedRepReward = slot.getRolledReputationReward() > 0 ? slot.getRolledReputationReward() : questEntry.getRepRewardMin();
                    detailScroll.addElement(new LabelElement(Spatials.positionXYZ(4, yOff, 1), (new TextComponent(" +" + displayedRepReward + " Reputation")).withStyle(Style.EMPTY.withColor(-8960)), LabelTextStyle.defaultStyle().shadowFromTextColor()));
                    int btnY = 186 - btnH - 4;
                    if (slot.isActive()) {
                        int questIdx = this.selectedQuest;
                        NineSliceButtonElement<?>[] btnRef = new NineSliceButtonElement[]{null};
                        NineSliceButtonElement<?> abandonBtn = new NineSliceButtonElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
                            if (btnRef[0] != null && btnRef[0].getTimeHeld() > (double)60.0F) {
                                ModNetwork.sendToServer(new ServerboundGreedQuestActionMessage(iskallia.vault.network.message.ServerboundGreedQuestActionMessage.Action.ABANDON, questIdx));
                                this.playClick();
                            }

                        });
                        btnRef[0] = abandonBtn;
                        abandonBtn.setRenderButtonHeld(() -> true);
                        abandonBtn.label(() -> (new TextComponent("Abandon")).withStyle(ChatFormatting.WHITE), LabelTextStyle.shadowFromTextColor().center());
                        this.addDetailEl((NineSliceButtonElement)abandonBtn.layout(this.translateWorldSpatial()));
                    } else if (slot.isAvailable()) {
                        int questIdx = this.selectedQuest;
                        NineSliceButtonElement<?> acceptBtn = (NineSliceButtonElement)this.addDetailEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
                            ModNetwork.sendToServer(new ServerboundGreedQuestActionMessage(iskallia.vault.network.message.ServerboundGreedQuestActionMessage.Action.ACCEPT, questIdx));
                            this.playClick();
                        })).layout(this.translateWorldSpatial()));
                        acceptBtn.setDisabled(questSlots.stream().anyMatch(GreedQuestSlot::isActive));
                        acceptBtn.label(() -> new TextComponent("Accept"), LabelTextStyle.shadowFromTextColor().center());
                    } else if (slot.isCompleted()) {
                        this.addDetailEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_DARK_GRAY)).layout(this.translateWorldSpatial()));
                        this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXY(rX, btnY + (btnH - 9) / 2 + 3), Spatials.size(rW, 9), (new TextComponent("Completed")).withStyle(Style.EMPTY.withColor(10616679)), LabelTextStyle.defaultStyle().center())).layout(this.translateWorldSpatial()));
                    } else if (slot.isAbandoned()) {
                        this.addDetailEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_DARK_GRAY)).layout(this.translateWorldSpatial()));
                        this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXY(rX, btnY + (btnH - 9) / 2 + 3), Spatials.size(rW, 9), (new TextComponent("Abandoned")).withStyle(ChatFormatting.RED), LabelTextStyle.defaultStyle().center())).layout(this.translateWorldSpatial()));
                    }

                }
            }
        }

        protected void containerTick() {
            super.containerTick();
            int hash = ClientGreedTreeData.getQuestSlots().hashCode();
            if (hash != this.lastQuestDataHash) {
                this.lastQuestDataHash = hash;
                this.selectedQuest = -1;
                this.clearContent();
                this.clearDetail();
                this.buildContent();
                this.requestLayout();
            }

        }
    }

    public static class Challenges extends CrystalTraderScreen {
        private static final int ACTIVE_SLOT_H = 19;
        private int selectedChallenge = -1;
        private int selectedHistoryIndex = -1;
        private int lastChallengeDataHash = 0;

        public Challenges(CrystalTraderContainer container, Inventory inventory, Component title) {
            super(container, inventory, title);
        }

        protected Tab getTab() {
            return CrystalTraderScreen.Tab.CHALLENGES;
        }

        protected String getTabTitle() {
            return "Challenges";
        }

        protected void init() {
            super.init();
            ModNetwork.sendToServer(new ServerboundGreedChallengeActionMessage(iskallia.vault.network.message.ServerboundGreedChallengeActionMessage.Action.POPULATE, -1));
        }

        protected void buildContent() {
            this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(7, 40).size(133, 142), ScreenTextures.INSET_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
            List<GreedChallengeSlot> challengeSlots = ClientGreedTreeData.getChallengeSlots();
            GreedChallengeSlot activeSlot = (GreedChallengeSlot)challengeSlots.stream().filter(GreedChallengeSlot::isAttempted).findFirst().orElse(null);
            int leftInnerX = 7;
            int leftInnerY = 17;
            int leftInnerW = 133;
            this.addContentEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(leftInnerX, leftInnerY).size(leftInnerW, 19), ScreenTextures.INSET_GREY_BACKGROUND)).layout(this.translateWorldSpatial()));
            if (activeSlot != null && ModConfigs.GREED_TRADER != null) {
                this.addContentEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(leftInnerX + 1, leftInnerY + 1).size(leftInnerW - 2, 17), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> this.selectChallenge(challengeSlots.indexOf(activeSlot)))).layout(this.translateWorldSpatial()));
                GreedChallengeEntry activeEntry = ModConfigs.GREED_TRADER.getChallengeEntryById(activeSlot.getChallengeId());
                if (activeEntry != null) {
                    int activeOrigIdx = challengeSlots.indexOf(activeSlot);
                    this.addContentEl((LabelElement)(new LabelElement(Spatials.positionXY(leftInnerX + 4, leftInnerY + 5), Spatials.size(leftInnerW - 8, 9), (new TextComponent(activeEntry.getDisplayName())).withStyle(Style.EMPTY.withColor(-11252677)), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
                    this.addContentEl((ClickableElement)(new ClickableElement(Spatials.positionXY(leftInnerX, leftInnerY).size(leftInnerW, 19), () -> this.selectChallenge(activeOrigIdx))).layout(this.translateWorldSpatial()));
                }
            } else {
                this.addContentEl((LabelElement)(new LabelElement(Spatials.positionXY(leftInnerX + 4, leftInnerY + 5), Spatials.size(leftInnerW - 8, 9), (new TextComponent("No active challenge")).withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle())).layout(this.translateWorldSpatial()));
            }

            int listY = leftInnerY + 19 + 4;
            int listH = 182 - listY;
            ScrollList scrollList = (ScrollList)this.addContentEl((ScrollList)(new ScrollList(Spatials.positionXY(leftInnerX, listY).size(leftInnerW, listH))).layout(this.translateWorldSpatial()));
            int contentW = leftInnerW - 9;
            int yOffset = 0;

            for(int i = 0; i < challengeSlots.size(); ++i) {
                final int challengeIndex = i;
                GreedChallengeSlot slot = (GreedChallengeSlot)challengeSlots.get(i);
                if (!slot.isAttempted() && ModConfigs.GREED_TRADER != null) {
                    GreedChallengeEntry entry = ModConfigs.GREED_TRADER.getChallengeEntryById(slot.getChallengeId());
                    if (entry != null) {
                        ClickableElement<?> item = new ClickableElement(Spatials.positionXY(1, yOffset).size(contentW, 19), () -> this.selectChallenge(challengeIndex));
                        item.addElement(new NineSliceButtonElement(Spatials.positionXY(0, 0).size(contentW, 19), ScreenTextures.RAISE_ENCHANTING_TABLE_BUTTON_TEXTURES, () -> {
                        }));
                        TextureAtlasRegion suffix = null;
                        if (slot.isComplete()) {
                            suffix = TextureAtlasRegion.of(ModTextureAtlases.QUESTS, VaultMod.id("gui/quests/check"));
                        }

                        int labelW = contentW - 8 - (suffix == null ? 0 : 14);
                        item.addElement(new LabelElement(Spatials.positionXY(4, 5), Spatials.size(labelW, 9), (new TextComponent(entry.getDisplayName())).withStyle(Style.EMPTY.withColor(-11252677)), LabelTextStyle.defaultStyle()));
                        if (suffix != null) {
                            item.addElement(new TextureAtlasElement(Spatials.positionXY(contentW - 4 - 15, 1), suffix));
                        }

                        scrollList.addElement(item);
                        yOffset += 19;
                    }
                }
            }

            List<GreedChallengeSlot> history = ClientGreedTreeData.getChallengeHistory();
            if (!history.isEmpty() && ModConfigs.GREED_TRADER != null) {
                for(int i = 0; i < history.size(); ++i) {
                    final int historyIndex = i;
                    GreedChallengeSlot histSlot = (GreedChallengeSlot)history.get(i);
                    GreedChallengeEntry histEntry = ModConfigs.GREED_TRADER.getChallengeEntryById(histSlot.getChallengeId());
                    if (histEntry != null) {
                        ClickableElement<?> histItem = new ClickableElement(Spatials.positionXY(1, yOffset).size(contentW, 19), () -> this.selectHistoryChallenge(historyIndex));
                        histItem.addElement(new NineSliceElement(Spatials.positionXY(0, 0).size(contentW, 19), ScreenTextures.BUTTON_EMPTY_DARK_GRAY));
                        TextureAtlasRegion histSuffix = null;
                        if (histSlot.isComplete()) {
                            histSuffix = TextureAtlasRegion.of(ModTextureAtlases.QUESTS, VaultMod.id("gui/quests/check"));
                        }

                        int histLabelW = contentW - 8 - (histSuffix == null ? 0 : 14);
                        histItem.addElement(new LabelElement(Spatials.positionXY(4, 5), Spatials.size(histLabelW, 9), (new TextComponent(histEntry.getDisplayName())).withStyle(Style.EMPTY.withColor(-11252677)), LabelTextStyle.defaultStyle()));
                        if (histSuffix != null) {
                            histItem.addElement(new TextureAtlasElement(Spatials.positionXY(contentW - 4 - 15, 1), histSuffix));
                        }

                        scrollList.addElement(histItem);
                        yOffset += 19;
                    }
                }
            }

            this.updateDetail();
        }

        private void selectChallenge(int index) {
            this.selectedChallenge = index;
            this.selectedHistoryIndex = -1;
            this.clearDetail();
            this.updateDetail();
            this.requestLayout();
        }

        private void selectHistoryChallenge(int index) {
            this.selectedHistoryIndex = index;
            this.selectedChallenge = -1;
            this.clearDetail();
            this.updateDetail();
            this.requestLayout();
        }

        private int getChallengeReputationReward() {
            if (ModConfigs.GREED == null) {
                return 0;
            } else {
                int greedTier = ClientGreedTreeData.getGreedTier();
                int threshold = ModConfigs.GREED.getReputationThreshold(greedTier);
                return (int)Math.ceil((double)threshold * (double)0.5F);
            }
        }

        private void updateDetail() {
            if (ModConfigs.GREED_TRADER != null) {
                List<GreedChallengeSlot> challengeSlots = ClientGreedTreeData.getChallengeSlots();
                List<GreedChallengeSlot> history = ClientGreedTreeData.getChallengeHistory();
                GreedChallengeSlot slot;
                if (this.selectedHistoryIndex >= 0 && this.selectedHistoryIndex < history.size()) {
                    slot = (GreedChallengeSlot)history.get(this.selectedHistoryIndex);
                } else {
                    if (this.selectedChallenge < 0 || this.selectedChallenge >= challengeSlots.size()) {
                        return;
                    }

                    slot = (GreedChallengeSlot)challengeSlots.get(this.selectedChallenge);
                }

                GreedChallengeEntry entry = ModConfigs.GREED_TRADER.getChallengeEntryById(slot.getChallengeId());
                if (entry != null) {
                    boolean hasActive = challengeSlots.stream().anyMatch(GreedChallengeSlot::isAttempted);
                    int rX = 152;
                    int rW = 139;
                    int btnH = 20;
                    this.addDetailEl((HeaderElement)(new HeaderElement(Spatials.positionXY(rX, 4).width(rW).height(20), (new TextComponent(entry.getDisplayName())).withStyle(ChatFormatting.WHITE))).layout(this.translateWorldSpatial()));
                    int detailY = 28;
                    int detailH = 189 - detailY - btnH - 3 - 3 - 3;
                    ScrollList detailScroll = (ScrollList)this.addDetailEl((ScrollList)(new ScrollList(Spatials.positionXY(rX, detailY).size(rW - 1, detailH))).layout(this.translateWorldSpatial()));
                    int innerW = rW - 7;
                    int yOff = 4;
                    String desc = entry.getDescription();
                    if (desc != null && !desc.isEmpty()) {
                        detailScroll.addElement(new LabelElement(Spatials.positionXY(4, yOff), Spatials.size(innerW - 8, 40), (new TextComponent(desc)).withStyle(ChatFormatting.DARK_GRAY), LabelTextStyle.defaultStyle().wrap()));
                        yOff += 44;
                    }

                    detailScroll.addElement(new LabelElement(Spatials.positionXYZ(4, yOff, 1), (new TextComponent("Rewards")).withStyle(ChatFormatting.WHITE), LabelTextStyle.defaultStyle().shadowFromTextColor()));
                    yOff += 12;
                    detailScroll.addElement(new LabelElement(Spatials.positionXYZ(4, yOff, 1), (new TextComponent(" +" + this.getChallengeReputationReward() + " Reputation")).withStyle(Style.EMPTY.withColor(-8960)), LabelTextStyle.defaultStyle().shadowFromTextColor()));
                    int btnY = 186 - btnH - 4;
                    int idx = this.selectedChallenge;
                    switch (slot.getState()) {
                        case ATTEMPTED:
                            int gap = 3;
                            int btnW = (rW - 2 - gap) / 2;
                            int rebuyCost = ModConfigs.GREED_TRADER.getChallengeRebuyCoinCost();
                            int rebuyX = rX + 1;
                            int abandonX = rX + 1 + btnW + gap;
                            NineSliceButtonElement<?> rebuyBtn = (NineSliceButtonElement)this.addDetailEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(rebuyX, btnY + 2).size(btnW, btnH), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
                                ModNetwork.sendToServer(new ServerboundGreedChallengeActionMessage(iskallia.vault.network.message.ServerboundGreedChallengeActionMessage.Action.REBUY, idx));
                                this.playClick();
                            })).layout(this.translateWorldSpatial()));
                            String rebuyText = "Rebuy x" + rebuyCost;
                            int textWidth = Minecraft.getInstance().font.width(rebuyText);
                            int iconSize = 9;
                            int iconGap = 2;
                            int totalWidth = textWidth + iconGap + iconSize;
                            int startX = rebuyX + (btnW - totalWidth) / 2;
                            this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXYZ(startX, btnY + 3 + (btnH - 9) / 2, 2), (new TextComponent(rebuyText)).withStyle(Style.EMPTY.withColor(-1)), LabelTextStyle.shadowFromTextColor())).layout(this.translateWorldSpatial()));
                            ItemStackDisplayElement<?> coinIcon = new ItemStackDisplayElement(Spatials.positionXYZ(startX + textWidth + iconGap, btnY + 2 + (btnH - iconSize) / 2, 10), new ItemStack(ModItems.GREED_COIN));
                            coinIcon.setScale((float)iconSize / 16.0F);
                            this.addDetailEl((ItemStackDisplayElement)coinIcon.layout(this.translateWorldSpatial()));
                            NineSliceButtonElement<?>[] abandonRef = new NineSliceButtonElement[]{null};
                            NineSliceButtonElement<?> abandonBtn = new NineSliceButtonElement(Spatials.positionXY(abandonX, btnY + 2).size(btnW, btnH), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
                                if (abandonRef[0] != null && abandonRef[0].getTimeHeld() > (double)60.0F) {
                                    ModNetwork.sendToServer(new ServerboundGreedChallengeActionMessage(iskallia.vault.network.message.ServerboundGreedChallengeActionMessage.Action.ABANDON, idx));
                                    this.playClick();
                                }

                            });
                            abandonRef[0] = abandonBtn;
                            abandonBtn.setRenderButtonHeld(() -> true);
                            abandonBtn.label(() -> (new TextComponent("Abandon")).withStyle(ChatFormatting.RED), LabelTextStyle.shadowFromTextColor().center());
                            this.addDetailEl((NineSliceButtonElement)abandonBtn.layout(this.translateWorldSpatial()));
                            break;
                        case AVAILABLE:
                            if (!hasActive) {
                                NineSliceButtonElement<?> acceptBtn = (NineSliceButtonElement)this.addDetailEl((NineSliceButtonElement)(new NineSliceButtonElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_TEXTURES, () -> {
                                    ModNetwork.sendToServer(new ServerboundGreedChallengeActionMessage(iskallia.vault.network.message.ServerboundGreedChallengeActionMessage.Action.ACCEPT, idx));
                                    this.playClick();
                                    this.onClose();
                                })).layout(this.translateWorldSpatial()));
                                acceptBtn.label(() -> new TextComponent("Accept"), LabelTextStyle.shadowFromTextColor().center());
                            }
                            break;
                        case COMPLETE:
                            this.addDetailEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_DARK_GRAY)).layout(this.translateWorldSpatial()));
                            this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXY(rX, btnY + (btnH - 9) / 2 + 3), Spatials.size(rW, 9), (new TextComponent("Completed")).withStyle(Style.EMPTY.withColor(10616679)), LabelTextStyle.defaultStyle().center())).layout(this.translateWorldSpatial()));
                            break;
                        case SKIPPED:
                            this.addDetailEl((NineSliceElement)(new NineSliceElement(Spatials.positionXY(rX + 1, btnY + 2).size(rW - 2, 20), ScreenTextures.BUTTON_EMPTY_DARK_GRAY)).layout(this.translateWorldSpatial()));
                            this.addDetailEl((LabelElement)(new LabelElement(Spatials.positionXY(rX, btnY + (btnH - 9) / 2 + 3), Spatials.size(rW, 9), (new TextComponent("Abandoned")).withStyle(ChatFormatting.RED), LabelTextStyle.defaultStyle().center())).layout(this.translateWorldSpatial()));
                    }

                }
            }
        }

        protected void containerTick() {
            super.containerTick();
            int hash = ClientGreedTreeData.getChallengeSlots().hashCode() + 31 * ClientGreedTreeData.getChallengeHistory().hashCode();
            if (hash != this.lastChallengeDataHash) {
                this.lastChallengeDataHash = hash;
                this.selectedChallenge = -1;
                this.selectedHistoryIndex = -1;
                this.clearContent();
                this.clearDetail();
                this.buildContent();
                this.requestLayout();
            }

        }
    }
}
