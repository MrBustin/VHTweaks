package xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.challenge.base.animation.AbstractChallengeAnimation;
import iskallia.vault.core.vault.challenge.base.animation.AnimationState;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.Template;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Rotation;

public class ProtectVillagerAnimation extends AbstractChallengeAnimation<ProtectVillagerAnimation.State> {
   public ProtectVillagerAnimation() {
      this.state = ProtectVillagerAnimation.State.IDLE;
   }

   protected ProtectVillagerAnimation.State getIdleState() {
      return ProtectVillagerAnimation.State.IDLE;
   }

   @Override
   public void onTick(ServerLevel world, BlockPos pos) {
      if (this.state != ProtectVillagerAnimation.State.IDLE && !this.state.getLocations().isEmpty()) {
         this.executeAnimationLogic(world, pos);
      }

      this.ticker++;
      if (this.isCompleted()) {
         this.onStop();
      }
   }

   private void executeAnimationLogic(ServerLevel world, BlockPos pos) {
      for (ProtectVillagerAnimation.SoundEffect soundEffect : this.state.getSoundEffects()) {
         if (this.ticker == soundEffect.getTick()) {
            for (ProtectVillagerAnimation.StructureLocation location : this.state.getLocations()) {
               world.playSound(
                  null, pos.offset(location.getOffset()), soundEffect.getSound(), SoundSource.BLOCKS, soundEffect.getVolume(), soundEffect.getPitch()
               );
            }
         }
      }

      for (ProtectVillagerAnimation.StructureLocation location : this.state.getLocations()) {
         List<String> steps = location.getAnimationSteps();
         if (!steps.isEmpty()) {
            int stepIndex = this.ticker / 20;
            if (stepIndex < steps.size() && this.ticker % 20 == 0) {
               String templateName = steps.get(stepIndex);
               Template template = VaultRegistry.TEMPLATE.getKey(VaultMod.id("vault/animations/protect_villager/" + templateName)).get(Version.latest());
               if (template != null) {
                  this.place(world, template, pos, settings -> {
                     settings.addProcessor(TileProcessor.rotate(location.getRotation(), BlockPos.ZERO, true));
                     settings.addProcessor(TileProcessor.translate(pos));
                     settings.addProcessor(TileProcessor.translate(location.getOffset()));
                  });
               }
            }
         }
      }
   }

   public static class SoundEffect {
      private final SoundEvent sound;
      private final float volume;
      private final float pitch;
      private final int tick;

      public SoundEffect(SoundEvent sound, float volume, float pitch, int tick) {
         this.sound = sound;
         this.volume = volume;
         this.pitch = pitch;
         this.tick = tick;
      }

      @Generated
      public SoundEvent getSound() {
         return this.sound;
      }

      @Generated
      public float getVolume() {
         return this.volume;
      }

      @Generated
      public float getPitch() {
         return this.pitch;
      }

      @Generated
      public int getTick() {
         return this.tick;
      }
   }

   public static enum State implements AnimationState {
      IDLE(0, List.of(), List.of()),
      CLOSE_ROOM(
         80,
         List.of(
            new ProtectVillagerAnimation.StructureLocation(new BlockPos(0, 0, 12), Rotation.NONE, List.of("barrier1", "barrier2", "barrier3", "barrier4")),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(0, 0, -12), Rotation.CLOCKWISE_180, List.of("barrier1", "barrier2", "barrier3", "barrier4")
            ),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(12, 0, 0), Rotation.CLOCKWISE_90, List.of("barrier1", "barrier2", "barrier3", "barrier4")
            ),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(-12, 0, 0), Rotation.COUNTERCLOCKWISE_90, List.of("barrier1", "barrier2", "barrier3", "barrier4")
            )
         ),
         List.of(
            new ProtectVillagerAnimation.SoundEffect(SoundEvents.PISTON_EXTEND, 1.0F, 0.8F, 0),
            new ProtectVillagerAnimation.SoundEffect(SoundEvents.IRON_DOOR_CLOSE, 1.2F, 1.0F, 80)
         )
      ),
      OPEN_ROOM(
         80,
         List.of(
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(0, 0, 12), Rotation.NONE, List.of("barrier4", "barrier3", "barrier2", "barrier1", "barrier0")
            ),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(0, 0, -12), Rotation.CLOCKWISE_180, List.of("barrier4", "barrier3", "barrier2", "barrier1", "barrier0")
            ),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(12, 0, 0), Rotation.CLOCKWISE_90, List.of("barrier4", "barrier3", "barrier2", "barrier1", "barrier0")
            ),
            new ProtectVillagerAnimation.StructureLocation(
               new BlockPos(-12, 0, 0), Rotation.COUNTERCLOCKWISE_90, List.of("barrier4", "barrier3", "barrier2", "barrier1", "barrier0")
            )
         ),
         List.of(
            new ProtectVillagerAnimation.SoundEffect(SoundEvents.PISTON_CONTRACT, 1.0F, 0.8F, 0),
            new ProtectVillagerAnimation.SoundEffect(SoundEvents.IRON_DOOR_OPEN, 1.2F, 1.0F, 80)
         )
      );

      private final int time;
      private final List<ProtectVillagerAnimation.StructureLocation> locations;
      private final List<ProtectVillagerAnimation.SoundEffect> soundEffects;

      private State(int time, List<ProtectVillagerAnimation.StructureLocation> locations, List<ProtectVillagerAnimation.SoundEffect> soundEffects) {
         this.time = time;
         this.locations = locations;
         this.soundEffects = soundEffects;
      }

      @Generated
      @Override
      public int getTime() {
         return this.time;
      }

      @Generated
      public List<ProtectVillagerAnimation.StructureLocation> getLocations() {
         return this.locations;
      }

      @Generated
      public List<ProtectVillagerAnimation.SoundEffect> getSoundEffects() {
         return this.soundEffects;
      }
   }

   public static class StructureLocation {
      private final BlockPos offset;
      private final Rotation rotation;
      private final List<String> animationSteps;

      public StructureLocation(BlockPos offset, Rotation rotation, List<String> animationSteps) {
         this.offset = offset;
         this.rotation = rotation;
         this.animationSteps = animationSteps;
      }

      @Generated
      public BlockPos getOffset() {
         return this.offset;
      }

      @Generated
      public Rotation getRotation() {
         return this.rotation;
      }

      @Generated
      public List<String> getAnimationSteps() {
         return this.animationSteps;
      }
   }
}
