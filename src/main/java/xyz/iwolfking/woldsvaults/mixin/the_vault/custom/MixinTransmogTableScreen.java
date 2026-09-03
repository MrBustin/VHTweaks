package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.DiscoveredModelSelectElement;
import iskallia.vault.client.gui.framework.element.TabElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.block.TransmogTableScreen;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModTextureAtlases;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransmogTableScreen.class, remap = false)
public abstract class MixinTransmogTableScreen extends AbstractElementContainerScreen<TransmogTableContainer> {

    @Shadow protected DiscoveredModelSelectElement<?> discoveredModelSelector;

    @Shadow protected abstract void refreshTabs();


    @Shadow protected static String lastSearch;

    public MixinTransmogTableScreen(TransmogTableContainer container, Inventory inventory,
                                    Component title,
                                    IElementRenderer elementRenderer,
                                    ITooltipRendererFactory<AbstractElementContainerScreen<TransmogTableContainer>> tooltipRendererFactory) {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }



    // if current filter is null => show mythic
    @Inject(method = "refreshTabs", at = @At("TAIL"))
    private void addMythicTab(CallbackInfo ci){
        boolean mythicSelected = false;
        try {
            mythicSelected = this.getClass().getDeclaredField("filter").get(null) == null; // is null
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        var selectedTexture = ScreenTextures.TAB_BACKGROUND_BOTTOM_SELECTED;
        var defaultTexture = ScreenTextures.TAB_BACKGROUND_BOTTOM;

        TextureAtlasRegion bg = mythicSelected ? selectedTexture :defaultTexture;

        int fullHeight = 187;
        int bottomCount = 3;
        int x = 4 + bottomCount * (bg.width() + 1);
        int y = fullHeight - (mythicSelected ? 4 : 0);

        var ttip = Tooltips.single(() -> (new TextComponent("Mythic")).withStyle(Style.EMPTY.withColor(VaultGearRarity.valueOf("MYTHIC").getColor())));

        TextureAtlasRegion icon = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon/mythic"));
        this.addElement(
                new TabElement<>(Spatials.positionXY(x, y),
                        new TextureAtlasElement<>(Spatials.zero(), bg),
                        new TextureAtlasElement<>(Spatials.positionXYZ(6, !mythicSelected ? 3 : 9 , 4), icon),
                        () -> {
                            try {
                                this.getClass().getDeclaredField("filter").set(null, null); // set null
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            this.discoveredModelSelector.refreshElements();
                            this.refreshTabs();
                        }
                )).tooltip(ttip).layout(this.translateWorldSpatial());
    }



    @Inject(method = "lambda$new$6", at = @At("HEAD"), cancellable = true)
    private static void addMythicTabTooltip(@Coerce Object active, Item item, ResourceLocation rl, CallbackInfoReturnable<Boolean> cir) {
        boolean mythicSelected = active == null; // is null
        if (mythicSelected) {
            if (ModConfigs.GEAR_MODEL_ROLL_RARITIES.getRarityOf(item, rl) != VaultGearRarity.valueOf("MYTHIC")) {
                cir.setReturnValue(false);
                return;
            }
            if (lastSearch.isEmpty()) {
                cir.setReturnValue(true);
                return;
            }
            cir.setReturnValue(ModDynamicModels.REGISTRIES.getAssociatedRegistry(item).map((reg) -> reg.get(rl).map((model) -> {
                String modelName = model.getDisplayName().toLowerCase();
                return modelName.contains(lastSearch.toLowerCase());
            }).orElse(false)).orElse(false));
        }
    }
}
