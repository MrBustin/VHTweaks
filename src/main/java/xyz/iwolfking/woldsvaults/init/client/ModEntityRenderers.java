package xyz.iwolfking.woldsvaults.init.client;


import iskallia.vault.entity.model.ModModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.client.renderers.MeteorArrowRenderer;
import xyz.iwolfking.woldsvaults.client.renderers.StormRenderer;
import xyz.iwolfking.woldsvaults.entities.client.CrystalVendorRenderer;
import xyz.iwolfking.woldsvaults.entities.client.JackBoxRenderer;
import xyz.iwolfking.woldsvaults.entities.client.VaultNecromancerRenderer;
import xyz.iwolfking.woldsvaults.entities.client.WitchskallRenderer;
import xyz.iwolfking.woldsvaults.entities.client.WoldBossRenderer;
import xyz.iwolfking.woldsvaults.entities.client.VaultMeteorRenderer;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.client.GenericWraithRenderer;
import xyz.iwolfking.woldsvaults.entities.thanksgiving.client.renderer.CranberrySlimeRenderer;
import xyz.iwolfking.woldsvaults.entities.thanksgiving.client.renderer.HaturkeyRenderer;
import xyz.iwolfking.woldsvaults.entities.thanksgiving.client.renderer.HostileTurkeyRenderer;
import xyz.iwolfking.woldsvaults.init.ModEntities;
import xyz.iwolfking.woldsvaults.items.gear.rang.VaultRangRenderer;


@OnlyIn(Dist.CLIENT)
public class ModEntityRenderers {
    public ModEntityRenderers() {
    }

    public static void register(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.WOLD, ctx -> new WoldBossRenderer(ctx, ModModelLayers.FIGHTER));
        EntityRenderers.register(ModEntities.VAULT_NECROMANCER, VaultNecromancerRenderer::new);
        EntityRenderers.register(ModEntities.CRYSTAL_VENDOR_ENTITY, CrystalVendorRenderer::new);
        EntityRenderers.register(ModEntities.WITCHSKALL, WitchskallRenderer::new);
        EntityRenderers.register(ModEntities.GREEN_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/green_ghost.png")));
        EntityRenderers.register(ModEntities.BROWN_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/brown_ghost.png")));
        EntityRenderers.register(ModEntities.BLACK_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/black_ghost.png")));
        EntityRenderers.register(ModEntities.BLUE_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/blue_ghost.png")));
        EntityRenderers.register(ModEntities.DARK_BLUE_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/dark_blue_ghost.png")));
        EntityRenderers.register(ModEntities.DARK_GRAY_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/dark_gray_ghost.png")));
        EntityRenderers.register(ModEntities.DARK_RED_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/dark_red_ghost.png")));
        EntityRenderers.register(ModEntities.PURPLE_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/purple_ghost.png")));
        EntityRenderers.register(ModEntities.YELLOW_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/yellow_ghost.png")));
        EntityRenderers.register(ModEntities.RED_GHOST, ctx -> new GenericWraithRenderer(ctx, WoldsVaults.id("textures/entity/red_ghost.png")));
        EntityRenderers.register(ModEntities.VAULT_RANG, VaultRangRenderer::new);
        EntityRenderers.register(ModEntities.HOSTILE_TURKEY, HostileTurkeyRenderer::new);
        EntityRenderers.register(ModEntities.CRANBERRY_SLIME, CranberrySlimeRenderer::new);
        EntityRenderers.register(ModEntities.HOSTILE_CHICKEN, ChickenRenderer::new);
        EntityRenderers.register(ModEntities.HOSTILE_SHEEP, SheepRenderer::new);
        EntityRenderers.register(ModEntities.HOSTILE_PIG, PigRenderer::new);
        EntityRenderers.register(ModEntities.HATURKIN, HaturkeyRenderer::new);
        EntityRenderers.register(ModEntities.JACK_BOX, JackBoxRenderer::new);
        EntityRenderers.register(ModEntities.CUSTOM_FANGS, EvokerFangsRenderer::new);
        EntityRenderers.register(ModEntities.VAULT_METEOR, VaultMeteorRenderer::new);
        EntityRenderers.register(ModEntities.METEOR_ARROW, MeteorArrowRenderer::new);
        EntityRenderers.register(ModEntities.STORM, StormRenderer::new);
    }
}
