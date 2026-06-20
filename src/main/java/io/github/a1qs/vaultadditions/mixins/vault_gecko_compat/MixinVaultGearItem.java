package io.github.a1qs.vaultadditions.mixins.vault_gecko_compat;

import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

@Mixin(VaultGearItem.class)
public interface MixinVaultGearItem extends VaultGearItem, IAnimatable {
    @Override
    default void registerControllers(AnimationData data) {
        for (DynamicModelRegistry<? extends DynamicModel<?>> registry : ModDynamicModels.REGISTRIES.getUniqueRegistries()) {
            registry.forEach((id, model) -> {
                VaultGeckoModel gecko = ModelUtil.getGeckoModel(model);
                if (gecko == null) {
                    return;
                }

                data.addAnimationController(new AnimationController<>(this, ((DynamicModel<?>) gecko).getId() + " Animation Controller", gecko.getTransitionTicks(), event -> {
                    DynamicModel<?> eventModel = ModelUtil.getDynamicModel(event.getExtraDataOfType(ItemStack.class).get(0));
                    if (eventModel == gecko) {
                        event.getController().setAnimation(gecko.getAnimation());
                        return PlayState.CONTINUE;
                    } else {
                        return PlayState.STOP;
                    }
                }));
            });
        }
    }

    @Override
    default AnimationFactory getFactory() {
        return ModelUtil.getAnimationFactory(this);
    }
}
