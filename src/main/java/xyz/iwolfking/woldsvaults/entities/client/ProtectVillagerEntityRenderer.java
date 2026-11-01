package xyz.iwolfking.woldsvaults.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.npc.Villager;

public class ProtectVillagerEntityRenderer extends VillagerRenderer {
    public ProtectVillagerEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void scale(Villager villager, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(3.0F, 3.0F, 3.0F);
    }
}
