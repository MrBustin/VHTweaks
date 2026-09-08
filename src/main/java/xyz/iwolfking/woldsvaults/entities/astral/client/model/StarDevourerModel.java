package xyz.iwolfking.woldsvaults.entities.astral.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelPart;
import xyz.iwolfking.woldsvaults.entities.astral.StarDevourerEntity;

public class StarDevourerModel extends HierarchicalModel<StarDevourerEntity> {
    private final ModelPart root;
    private final RavagerModel internalRavagerModel;

    public StarDevourerModel(ModelPart part) {
        this.root = part;
        this.internalRavagerModel = new RavagerModel(part);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(StarDevourerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.internalRavagerModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void prepareMobModel(StarDevourerEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.internalRavagerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}