package xyz.iwolfking.woldsvaults.client.events;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.client.renderers.IdonasBarrageArrowRenderer;
import xyz.iwolfking.woldsvaults.client.renderers.IdonasBarrageRenderer;
import xyz.iwolfking.woldsvaults.init.additions.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Replace NoopRenderer with the correct renderer for your entity
        event.registerEntityRenderer(ModEntities.THORN_CLOUD, NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.IDONAS_BARRAGE_ARROW, IdonasBarrageArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.IDONAS_BARRAGE, IdonasBarrageRenderer::new);
    }
}
