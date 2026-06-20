package io.github.a1qs.vaultadditions.vault.skill.ability;

import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModEffects;
import io.github.a1qs.vaultadditions.util.SoundChoice;
import io.github.a1qs.vaultadditions.vault.gear.attribute.special.LegacyManaShieldAbsorptionModification;
import io.github.a1qs.vaultadditions.vault.gear.effect.AbilitySoundTransmogEffect;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.FloatRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
import iskallia.vault.init.ModAbilities;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mana.Mana;
import iskallia.vault.mana.ManaAction;
import iskallia.vault.skill.ability.effect.ManaShieldAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.skill.ability.effect.spi.core.ToggleManaAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(
        modid = VaultAdditions.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class LegacyManaShieldAbility extends ToggleManaAbility {
    private float percentageDamageAbsorbed;
    private float manaPerDamageScalar;

    public float getPercentageDamageAbsorbed() {
        return this.percentageDamageAbsorbed;
    }

    public float getManaPerDamageScalar() {
        return manaPerDamageScalar;
    }

    private ToggleAbilityEffect getEffect() {
        return ModEffects.LEGACY_MANA_SHIELD;
    }

    @Override
    protected Ability.ActionResult doToggle(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            if (this.isActive()) {
                this.getEffect().addTo(player, 0);
                return Ability.ActionResult.successCooldownDeferred();
            } else {
                player.removeEffect(this.getEffect());
                return Ability.ActionResult.successCooldownImmediate();
            }
        }).orElse(Ability.ActionResult.fail());
    }

    @Override
    protected void doToggleSound(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent(player -> {
            if(!this.isActive()) return;
            SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.MANA_SHIELD, new SoundChoice(ModSounds.MANA_SHIELD, 0.2F, 0.2F));
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
            player.playNotifySound(sound.event(), SoundSource.MASTER, sound.volume(), sound.pitch());
        });
    }

    @Override
    public Ability.TickResult doInactiveTick(SkillContext context) {
        return context.getSource().as(ServerPlayer.class).map(player -> {
            if (player.hasEffect(this.getEffect())) {
                player.removeEffect(this.getEffect());
            }

            return Ability.TickResult.PASS;
        }).orElse(Ability.TickResult.PASS);
    }

    @Override
    public void onRemove(SkillContext context) {
        context.getSource().as(ServerPlayer.class).ifPresent((entity) -> entity.removeEffect(this.getEffect()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void on(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);
            for (LegacyManaShieldAbility ability : abilities.getAll(LegacyManaShieldAbility.class, Skill::isUnlocked)) {
                if (player.hasEffect(ability.getEffect())) {
                    float percentageDamageAbsorbed = Mth.clamp(ability.getPercentageDamageAbsorbed(), 0.0F, 1.0F);
                    float manaCostPerDamage = ability.getManaPerDamageScalar();

                    for (ConfiguredModification<LegacyManaShieldAbsorptionModification, FloatRangeConfig, FloatValue> mod : SpecialAbilityModification.getModifications(
                            player, LegacyManaShieldAbsorptionModification.class
                    )) {
                        manaCostPerDamage = mod.modification().adjustAbsorptionDamageCost(mod.value().getValue(), manaCostPerDamage);
                    }

                    manaCostPerDamage = Math.max(manaCostPerDamage, 1.0E-5F);
                    float manaUsed = Math.min(event.getAmount() * percentageDamageAbsorbed * manaCostPerDamage, Mana.get(player));
                    float damageAbsorbed = manaUsed / manaCostPerDamage;
                    if (Mth.equal(damageAbsorbed, 0.0F)) {
                        return;
                    }
                    if (Mth.equal(damageAbsorbed, event.getAmount())) {
                        event.setCanceled(true);
                    } else {
                        event.setAmount(event.getAmount() - damageAbsorbed);
                    }

                    float mana = Mana.decrease(player, ManaAction.PLAYER_ACTION, manaUsed);
                    ability.onDamageAbsorbed(player, damageAbsorbed);
                    SoundChoice def = new SoundChoice(ModSounds.MANA_SHIELD_HIT, 0.1F,  1.25F + -0.5F * (mana / Mana.getMax(player)));
                    SoundChoice sound = AbilitySoundTransmogEffect.getSound(player, ModAbilities.MANA_SHIELD, 1, def);
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.event(), SoundSource.PLAYERS, sound.volume(), sound.pitch());
                }
            }
        }
    }

    protected void onDamageAbsorbed(ServerPlayer player, float amount) {
    }

    protected void onEffectRemoved(ServerPlayer player) {
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        Adapters.FLOAT.writeBits(Float.valueOf(this.percentageDamageAbsorbed), buffer);
        Adapters.FLOAT.writeBits(Float.valueOf(this.manaPerDamageScalar), buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.percentageDamageAbsorbed = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.manaPerDamageScalar = Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            Adapters.FLOAT.writeNbt(Float.valueOf(this.percentageDamageAbsorbed)).ifPresent(tag -> nbt.put("percentageDamageAbsorbed", tag));
            Adapters.FLOAT.writeNbt(Float.valueOf(this.manaPerDamageScalar)).ifPresent(tag -> nbt.put("manaPerDamageScalar", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.percentageDamageAbsorbed = Adapters.FLOAT.readNbt(nbt.get("percentageDamageAbsorbed")).orElse(0.0F);
        this.manaPerDamageScalar = Adapters.FLOAT.readNbt(nbt.get("manaPerDamageScalar")).orElse(0.0F);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            Adapters.FLOAT.writeJson(Float.valueOf(this.percentageDamageAbsorbed)).ifPresent(element -> json.add("percentageDamageAbsorbed", element));
            Adapters.FLOAT.writeJson(Float.valueOf(this.manaPerDamageScalar)).ifPresent(element -> json.add("manaPerDamageScalar", element));
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.percentageDamageAbsorbed = Adapters.FLOAT.readJson(json.get("percentageDamageAbsorbed")).orElse(0.0F);
        this.manaPerDamageScalar = Adapters.FLOAT.readJson(json.get("manaPerDamageScalar")).orElse(0.0F);
    }

    public static class LegacyManaShieldEffect extends ToggleAbilityEffect {
        public LegacyManaShieldEffect(int color, ResourceLocation resourceLocation) {
            super(ManaShieldAbility.class, color, resourceLocation);
        }

        @Override
        protected void removeAttributeModifiers(ServerPlayer player, AttributeMap attributeMap, int amplifier) {
            super.removeAttributeModifiers(player, attributeMap, amplifier);
            if (!player.hasEffect(this)) {
                AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

                for (LegacyManaShieldAbility ability : abilities.getAll(LegacyManaShieldAbility.class, Skill::isUnlocked)) {
                    ability.onEffectRemoved(player);
                }
            }
        }
    }

}
