package io.github.a1qs.vaultadditions.mixins.skillaltarfix;

import iskallia.vault.util.nbt.NBTHelper;
import iskallia.vault.world.data.SkillAltarData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Mixin(SkillAltarData.class)
public class SkillAltarDataMixin {
    @Redirect(method = "save",at = @At(value = "INVOKE", target = "Liskallia/vault/util/nbt/NBTHelper;writeMap(Lnet/minecraft/nbt/CompoundTag;Ljava/lang/String;Ljava/util/Map;Ljava/util/function/Function;Ljava/util/function/Function;)V"))
    public <K extends UUID, C extends Integer, W extends SkillAltarData.SkillTemplate, V extends Map<C, W>> void saveNPE(CompoundTag tag, String key, Map<K, V> map, Function<K, String> getStringKey, Function<V, Tag> getNbtValue) {
        HashMap<K, V> nullSafeMap = new HashMap<>();
        if (map != null) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    HashMap<C, W> nullSafeInnerMap = new HashMap<>();
                    for (Map.Entry<C, W> innerEntry : entry.getValue().entrySet()) {
                        if (innerEntry.getKey() != null && innerEntry.getValue() != null) {
                            nullSafeInnerMap.put(innerEntry.getKey(), innerEntry.getValue());
                        }
                    }
                    nullSafeMap.put(entry.getKey(), (V) nullSafeInnerMap);
                }
            }
        }
        NBTHelper.writeMap(tag, key, nullSafeMap, getStringKey, getNbtValue);
    }
}
