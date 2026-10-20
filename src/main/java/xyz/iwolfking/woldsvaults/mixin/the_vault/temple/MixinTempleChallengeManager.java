package xyz.iwolfking.woldsvaults.mixin.the_vault.temple;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.temple.TempleChallengeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleAnimation;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.TempleAnimationHolder;

import java.util.Optional;

@Mixin(value = TempleChallengeManager.class, remap = false)
public abstract class MixinTempleChallengeManager implements TempleAnimationHolder {
    @Shadow private TempleChallengeManager.Phase phase;

    @Unique private final TempleAnimation woldsvaults$animation = new TempleAnimation();
    @Unique private TempleChallengeManager.Phase woldsvaults$previousPhase = TempleChallengeManager.Phase.IDLE;

    @Override
    public TempleAnimation woldsvaults$getAnimation() {
        return this.woldsvaults$animation;
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private void woldsvaults$waitForOpeningAnimation(ServerLevel world, CallbackInfo ci) {
        if (this.phase != TempleChallengeManager.Phase.COMPLETED) return;

        if (this.woldsvaults$animation.getState() != TempleAnimation.State.OPEN_ROOM) {
            this.woldsvaults$animation.onStart(TempleAnimation.State.OPEN_ROOM);
        }
        if (!this.woldsvaults$animation.isCompleted()) {
            this.woldsvaults$animation.onTick(world, ((TempleChallengeManager) (Object) this).getPos());
            ci.cancel();
        }
    }

    @Inject(method = "onTick", at = @At("TAIL"))
    private void woldsvaults$tickAnimation(ServerLevel world, CallbackInfo ci) {
        if (this.woldsvaults$previousPhase != TempleChallengeManager.Phase.ACTIVATING
                && this.phase == TempleChallengeManager.Phase.ACTIVATING) {
            this.woldsvaults$animation.onStart(TempleAnimation.State.CLOSE_ROOM);
        } else if (this.woldsvaults$previousPhase != TempleChallengeManager.Phase.COMPLETED
                && this.phase == TempleChallengeManager.Phase.COMPLETED) {
            this.woldsvaults$animation.onStart(TempleAnimation.State.OPEN_ROOM);
        }
        this.woldsvaults$animation.onTick(world, ((TempleChallengeManager) (Object) this).getPos());
        this.woldsvaults$previousPhase = this.phase;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void woldsvaults$writeGates(CallbackInfoReturnable<Optional<CompoundTag>> cir) {
        cir.getReturnValue().ifPresent(tag -> {
            ListTag gates = new ListTag();
            this.woldsvaults$animation.getGates().forEach((pos, rotation) -> {
                CompoundTag gate = new CompoundTag();
                Adapters.BLOCK_POS.writeNbt(pos).ifPresent(value -> gate.put("pos", value));
                Adapters.INT.writeNbt(rotation.ordinal()).ifPresent(value -> gate.put("rotation", value));
                gates.add(gate);
            });
            tag.put("gates", gates);
        });
    }

    @Inject(method = "readNbt(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void woldsvaults$readGates(CompoundTag tag, CallbackInfo ci) {
        this.woldsvaults$animation.getGates().clear();
        ListTag gates = tag.getList("gates", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < gates.size(); i++) {
            CompoundTag gate = gates.getCompound(i);
            BlockPos gatePos = Adapters.BLOCK_POS.readNbt(gate.get("pos")).orElse(null);
            int rotation = Adapters.INT.readNbt(gate.get("rotation")).orElse(0).intValue();
            if (gatePos != null) {
                Rotation[] rotations = Rotation.values();
                this.woldsvaults$animation.addGate(gatePos, rotations[Math.floorMod(rotation, rotations.length)]);
            }
        }
        this.woldsvaults$previousPhase = this.phase;
    }
}
