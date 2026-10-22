package xyz.iwolfking.woldsvaults.mixin.the_vault.temple;

import iskallia.vault.block.entity.challenge.TempleControllerTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.temple.TempleChallengeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleAnimationHolder;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(value = TempleControllerTileEntity.class, remap = false)
public class MixinTempleControllerTileEntity {
    @Unique private final Map<BlockPos, Rotation> woldsvaults$gates = new LinkedHashMap<>();

    @Inject(method = "createManager", at = @At("RETURN"))
    private void woldsvaults$copyGatesToManager(CallbackInfoReturnable<TempleChallengeManager> cir) {
        TempleAnimationHolder holder = (TempleAnimationHolder) cir.getReturnValue();
        this.woldsvaults$gates.forEach(holder.woldsvaults$getAnimation()::addGate);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void woldsvaults$saveGates(CompoundTag tag, CallbackInfo ci) {
        ListTag gates = new ListTag();
        this.woldsvaults$gates.forEach((pos, rotation) -> {
            CompoundTag gate = new CompoundTag();
            Adapters.BLOCK_POS.writeNbt(pos).ifPresent(value -> gate.put("pos", value));
            Adapters.INT.writeNbt(rotation.ordinal()).ifPresent(value -> gate.put("rotation", value));
            gates.add(gate);
        });
        tag.put("gates", gates);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void woldsvaults$loadGates(CompoundTag tag, CallbackInfo ci) {
        this.woldsvaults$gates.clear();
        ListTag gates = tag.getList("gates", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < gates.size(); i++) {
            CompoundTag gate = gates.getCompound(i);
            BlockPos gatePos = Adapters.BLOCK_POS.readNbt(gate.get("pos")).orElse(null);
            int rotation = Adapters.INT.readNbt(gate.get("rotation")).orElse(0).intValue();
            if (gatePos != null) {
                Rotation[] rotations = Rotation.values();
                this.woldsvaults$gates.put(gatePos, rotations[Math.floorMod(rotation, rotations.length)]);
            }
        }
    }
}
