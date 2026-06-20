package io.github.a1qs.vaultadditions.vault.skill.power;

import com.google.gson.JsonObject;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.data.PlayerPowersData;
import io.github.a1qs.vaultadditions.init.vault.ModGearAttributes;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FallReductionPower extends LearnableSkill {
    private float damageReduction;

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        buffer.writeFloat(damageReduction);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.damageReduction = Adapters.FLOAT.readBits(buffer).orElseThrow();
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            nbt.putFloat("damageReduction", this.damageReduction);
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.damageReduction = Adapters.FLOAT.readNbt(nbt.get("damageReduction")).orElseThrow();
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            json.addProperty("damageReduction", this.damageReduction);
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.damageReduction = Adapters.FLOAT.readJson(json.get("damageReduction")).orElseThrow();
    }

    public float getDamageReduction() {
        return damageReduction;
    }

    @SubscribeEvent
    public static void onLivingEntityHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof Player player) || player.getServer() == null || !event.getSource().isFall()) {
            return;
        }

        float damageReduction = 0.0F;
        PowerTree expertises = PlayerPowersData.get(player.getServer()).getPowers(player);
        for (FallReductionPower power : expertises.getAll(FallReductionPower.class, Skill::isUnlocked)) {
            damageReduction = power.getDamageReduction();
        }

        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
        damageReduction += snapshot.getAttributeValue(ModGearAttributes.FALL_DAMAGE_REDUCTION_PERCENT, VaultGearAttributeTypeMerger.floatSum());
        damageReduction = Math.min(damageReduction, 1.0F);

        event.setAmount(event.getAmount() * (1.0F - damageReduction));
    }
}
