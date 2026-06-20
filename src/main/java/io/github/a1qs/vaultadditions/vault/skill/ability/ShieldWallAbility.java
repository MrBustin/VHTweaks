package io.github.a1qs.vaultadditions.vault.skill.ability;

import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModEffects;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityDamageBlockEvent;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.calc.BlockChanceHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShieldWallAbility extends InstantManaAbility {
    private int durationTicks;

    protected MobEffect getEffect() {
        return ModEffects.SHIELD_WALL;
    }

    public int getDurationTicks() {
        return this.durationTicks;
    }

    protected Ability.ActionResult doAction(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map((player) -> {
            if (player.hasEffect(this.getEffect())) {
                return ActionResult.fail();
            } else {
                int duration = this.getDurationTicks();
                MobEffectInstance newEffect = new MobEffectInstance(this.getEffect(), duration, 0, false, false, true);
                player.addEffect(newEffect);
                return ActionResult.successCooldownDeferred();
            }
        }).orElse(ActionResult.fail());
    }

    protected void doSound(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent((player) -> {
            player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 1.0F);
            player.playNotifySound(ModSounds.MANA_SHIELD, SoundSource.PLAYERS, 0.2F, 0.75F); // vol, pitch
        });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onHurt(LivingHurtEvent event) {
        if (canBlock(event.getEntityLiving(), event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onAttack(LivingAttackEvent event) {
        if (canBlock(event.getEntityLiving(), event.getSource())) {
            event.getEntityLiving().playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + event.getEntityLiving().level.random.nextFloat() * 0.4F);
            event.setCanceled(true);
        }
    }

    public static boolean canBlock(@Nullable LivingEntity entity, @Nullable DamageSource source) {
        if (entity instanceof ServerPlayer player) {
            if(player.getOffhandItem().getItem() instanceof ShieldItem) {
                AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

                for(ShieldWallAbility ability : abilities.getAll(ShieldWallAbility.class, Skill::isUnlocked)) {
                    if (player.hasEffect(ability.getEffect()) && (source == null || !source.isBypassInvul())) {
                        if (player.getOffhandItem().getItem() instanceof VaultGearItem) {
                            VaultGearData gearData = VaultGearData.read(player.getOffhandItem());
                            gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
                                    .flatMap(ModDynamicModels.Shields.REGISTRY::get)
                                    .ifPresent(shieldModel -> shieldModel.onBlocked(entity, source));
                        }
                        entity.getLevel().broadcastEntityEvent(entity, (byte) 29);
                        CommonEvents.ENTITY_DAMAGE_BLOCK.invoke(new EntityDamageBlockEvent.Data(false, source, entity));
                        BlockChanceHelper.setPlayerBlocking(player);
                        return true;
                    }
                }

            }
        }
        return false;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.durationTicks, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map((nbt) -> {
            Adapters.INT.writeNbt(this.durationTicks).ifPresent((tag) -> nbt.put("durationTicks", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map((json) -> {
            Adapters.INT.writeJson(this.durationTicks).ifPresent((element) -> json.add("durationTicks", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
    }

    public static class ShieldWallEffect extends MobEffect {
        public ShieldWallEffect(MobEffectCategory typeIn, int liquidColorIn, ResourceLocation id) {
            super(typeIn, liquidColorIn);
            this.setRegistryName(id);
        }

        @Override @ParametersAreNonnullByDefault
        public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
            if (livingEntity instanceof ServerPlayer player) {
                PlayerAbilitiesData.setAbilityOnCooldown(player, ShieldWallAbility.class);
            }
            super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
        }
    }
}
