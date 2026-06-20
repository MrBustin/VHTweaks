package io.github.a1qs.vaultadditions.mixins.oversized_chunks;

import iskallia.vault.block.entity.VaultOreTileEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VaultOreTileEntity.class)
public class VaultOreTileEntityMixin {
    @Shadow @Final private List<String> templateTags;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    public void clearTemplateTags(CompoundTag nbt, CallbackInfo ci) {
        this.templateTags.clear();
    }
}