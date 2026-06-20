package io.github.a1qs.vaultadditions.vault.gear.gecko.armor;

import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;

public class GeckoArmorModel extends ArmorModel implements VaultGeckoModel {
    private final ResourceLocation modelPath;
    private final ResourceLocation texturePath;
    private final ResourceLocation animationPath;

    private final AnimationBuilder animation;
    private final float transitionTicks;

    public GeckoArmorModel(String id, String displayName, String animationName, float transitionTicks) {
        super(VaultMod.id("gear/armor/" + id), displayName);

        this.modelPath = VaultMod.id("geo/armor/" + id + ".geo.json");
        this.texturePath = VaultMod.id("textures/item/gear/armor/" + id + "/texture.png");
        this.animationPath = VaultMod.id("animations/armor/" + id + ".animation.json");

        this.animation = new AnimationBuilder().addAnimation(animationName, ILoopType.EDefaultLoopTypes.LOOP);
        this.transitionTicks = transitionTicks;
    }

    @Override
    public ResourceLocation getModelPath() {
        return this.modelPath;
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
        return this.animation;
    }

    @Override
    public float getTransitionTicks() {
        return this.transitionTicks;
    }
}
