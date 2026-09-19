package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.NineSliceTextInputElement;
import iskallia.vault.client.gui.framework.element.TabElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.TextBorder;
import iskallia.vault.client.gui.screen.block.PlayerCustomisationStationScreen;
import iskallia.vault.container.PlayerCustomisationStationContainer;
import iskallia.vault.init.ModTextureAtlases;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.client.data.ClientDiscoveredCapesData;
import xyz.iwolfking.woldsvaults.client.gui.framework.element.CapeSelectorElement;
import xyz.iwolfking.woldsvaults.client.gui.framework.element.PlayerPreviewElement;
import xyz.iwolfking.woldsvaults.configs.PlayerCapesConfig;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.network.message.SelectCapeMessage;
import xyz.iwolfking.woldsvaults.api.extension.CapeDiscoveryConfigExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Mixin(value = PlayerCustomisationStationScreen.class, remap = false)
public abstract class MixinPlayerCustomisationStationScreen
        extends AbstractElementContainerScreen<PlayerCustomisationStationContainer> {

    @Shadow @Final private List<IElement> prefixTabElements;
    @Shadow @Final private List<IElement> suffixTabElements;
    @Shadow @Final private List<IElement> emblemTabElements;
    @Shadow @Final private List<IElement> trailTabElements;
    @Shadow protected abstract void refreshTabs();

    @Unique private final List<IElement> woldsvaults$capeTabElements = new ArrayList<>();
    @Unique private boolean woldsvaults$capeTabSelected;
    @Unique private String woldsvaults$selectedCapeId;
    @Unique private String woldsvaults$capeSearch = "";
    @Unique private CapeSelectorElement woldsvaults$capeSelector;
    @Unique private NineSliceTextInputElement<?> woldsvaults$capeSearchInput;

    protected MixinPlayerCustomisationStationScreen(PlayerCustomisationStationContainer container, Inventory inventory,
                                                     Component title, IElementRenderer elementRenderer,
                                                     ITooltipRendererFactory<AbstractElementContainerScreen<PlayerCustomisationStationContainer>> tooltipRendererFactory) {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void woldsvaults$buildCapeTab(PlayerCustomisationStationContainer container, Inventory inventory,
                                          Component title, CallbackInfo ci) {
        this.woldsvaults$selectedCapeId = ClientDiscoveredCapesData.getSelectedCape();

        this.woldsvaults$capeSearchInput = new NineSliceTextInputElement<>(
                Spatials.positionXY(8, 8).size(147, 14),
                ScreenTextures.INSET_GREY_BACKGROUND,
                TextBorder.DEFAULT_FONT.get())
                .setPlaceholderText("Search...")
                .setTextColor(0xFF_3f3f3f)
                .onTextChanged(text -> {
                    if (this.woldsvaults$capeTabSelected) {
                        this.woldsvaults$capeSearch = text;
                        this.woldsvaults$refreshCapeSelector();
                    }
                }).layout(this.translateWorldSpatial());
        this.addElement(this.woldsvaults$addCapeElement(this.woldsvaults$capeSearchInput));

        this.woldsvaults$capeSelector = new CapeSelectorElement(
                Spatials.positionXY(8, 24).size(147, 122),
                this.woldsvaults$buildCapeEntries(),
                entry -> {
                    this.woldsvaults$selectedCapeId = entry.id();
                    ModNetwork.sendToServer(new SelectCapeMessage(entry.id(), entry.id() != null));
                });
        this.addElement(this.woldsvaults$addCapeElement(this.woldsvaults$capeSelector).layout(this.translateWorldSpatial()));

        if (this.woldsvaults$selectedCapeId != null) {
            this.woldsvaults$capeSelector.selectEntry(entry -> Objects.equals(entry.id(), this.woldsvaults$selectedCapeId));
        }

        this.addElement(this.woldsvaults$addCapeElement(new PlayerPreviewElement<>(
                Spatials.positionXY(180, 8).size(80, 122)).layout(this.translateWorldSpatial())));
        this.woldsvaults$setElementsState(this.woldsvaults$capeTabElements, false);
    }

    @Inject(method = "refreshTabs", at = @At("TAIL"))
    private void woldsvaults$addCapeTab(CallbackInfo ci) {
        TextureAtlasRegion background = this.woldsvaults$capeTabSelected
                ? ScreenTextures.TAB_BACKGROUND_TOP_SELECTED
                : ScreenTextures.TAB_BACKGROUND_TOP;
        TextureAtlasRegion icon = TextureAtlasRegion.of(ModTextureAtlases.SCREEN,
                VaultMod.id("gui/screen/tab_icon_cape"));
        int tabY = this.woldsvaults$capeTabSelected ? -28 : -24;

        this.addElement(new TabElement<>(
                Spatials.positionXY(8 + 4 * 29, tabY),
                new TextureAtlasElement<>(Spatials.zero(), background),
                new TextureAtlasElement<>(Spatials.positionXYZ(6, this.woldsvaults$capeTabSelected ? 6 : 3, 1), icon),
                this::woldsvaults$showCapeTab)
                .tooltip(Tooltips.single(() -> new TextComponent("Capes")))
                .layout(this.translateWorldSpatial()));
    }

    @Inject(method = "showTab", at = @At("HEAD"))
    private void woldsvaults$onNativeTabSelected(@Coerce Object tab, CallbackInfo ci) {
        this.woldsvaults$capeTabSelected = false;
        this.woldsvaults$setElementsState(this.woldsvaults$capeTabElements, false);
    }

    @Unique
    private void woldsvaults$showCapeTab() {
        this.woldsvaults$capeTabSelected = true;
        this.woldsvaults$capeSearch = "";
        this.woldsvaults$capeSearchInput.setText("");
        this.woldsvaults$setElementsState(this.prefixTabElements, false);
        this.woldsvaults$setElementsState(this.suffixTabElements, false);
        this.woldsvaults$setElementsState(this.emblemTabElements, false);
        this.woldsvaults$setElementsState(this.trailTabElements, false);
        this.woldsvaults$setElementsState(this.woldsvaults$capeTabElements, true);
        this.woldsvaults$refreshCapeSelector();
        this.refreshTabs();
    }

    @Unique
    private <E extends IElement> E woldsvaults$addCapeElement(E element) {
        this.woldsvaults$capeTabElements.add(element);
        return element;
    }

    @Unique
    private void woldsvaults$setElementsState(List<IElement> elements, boolean active) {
        for (IElement element : elements) {
            element.setEnabled(active);
            if (element instanceof IRenderedElement renderedElement) {
                renderedElement.setVisible(active);
            }
        }
    }

    @Unique
    private List<CapeSelectorElement.CapeEntry> woldsvaults$buildCapeEntries() {
        List<CapeSelectorElement.CapeEntry> entries = new ArrayList<>();
        entries.add(new CapeSelectorElement.CapeEntry(null, "None", null, true));
        PlayerCapesConfig config = ModConfigs.PLAYER_CAPES;
        if (config == null) {
            return entries;
        }

        Set<String> discovered = ClientDiscoveredCapesData.getDiscoveredCapes();
        String search = this.woldsvaults$capeSearch.toLowerCase(java.util.Locale.ROOT);
        for (Map.Entry<String, PlayerCapesConfig.Cape> configEntry : config.getAll().entrySet()) {
            PlayerCapesConfig.Cape cape = configEntry.getValue();
            if (search.isEmpty() || cape.getDisplayName().toLowerCase(java.util.Locale.ROOT).contains(search)) {
                boolean isDiscovered = discovered.contains(configEntry.getKey());
                Component requirementText = null;
                if (!isDiscovered && iskallia.vault.init.ModConfigs.CUSTOMISATION_DISCOVERY
                        instanceof CapeDiscoveryConfigExtension extension) {
                    requirementText = extension.woldsvaults$getCapeRequirementText(configEntry.getKey());
                }
                entries.add(new CapeSelectorElement.CapeEntry(configEntry.getKey(), cape.getDisplayName(),
                        cape.getDescription(), isDiscovered, requirementText));
            }
        }
        return entries;
    }

    @Unique
    private void woldsvaults$refreshCapeSelector() {
        if (this.woldsvaults$capeSelector == null) {
            return;
        }
        this.woldsvaults$capeSelector.setEntries(this.woldsvaults$buildCapeEntries());
        if (this.woldsvaults$selectedCapeId != null) {
            this.woldsvaults$capeSelector.selectEntry(entry -> Objects.equals(entry.id(), this.woldsvaults$selectedCapeId));
        }
    }
}
