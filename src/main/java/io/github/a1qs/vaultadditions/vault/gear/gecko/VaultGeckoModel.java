package io.github.a1qs.vaultadditions.vault.gear.gecko;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public interface VaultGeckoModel {
    ResourceLocation getModelPath();
    ResourceLocation getTexturePath();
    ResourceLocation getAnimationPath();
    AnimationBuilder getAnimation();
    float getTransitionTicks();
}
