package io.github.a1qs.vaultadditions.vault.gear.gecko.item;

import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;

public class GeckoHandHeldModel extends HandHeldModel implements VaultGeckoModel {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationPath;

    private final AnimationBuilder animation;
    private final float transitionTicks;

    public GeckoHandHeldModel(String id, String type, String displayName, String animationName, float transitionTicks) {
        super(VaultMod.id("gear/" + type + "/" + id), displayName);

        this.modelPath = VaultMod.id("geo/" + type + "/" + id + ".geo.json");
        this.texturePath = VaultMod.id("textures/item/gear/" + type + "/" + id + ".png");
        this.animationPath = VaultMod.id("animations/" + type + "/" + id + ".animation.json");

        this.animation = new AnimationBuilder().addAnimation(animationName, ILoopType.EDefaultLoopTypes.LOOP);
        this.transitionTicks = transitionTicks;
    }

    @Override
    public ResourceLocation getModelPath() {
        return modelPath;
    }

    @Override
    public ResourceLocation getTexturePath() {
        return texturePath;
    }

    @Override
    public ResourceLocation getAnimationPath() {
        return animationPath;
    }

    @Override
    public AnimationBuilder getAnimation() {
        return animation;
    }

    @Override
    public float getTransitionTicks() {
        return transitionTicks;
    }
}
