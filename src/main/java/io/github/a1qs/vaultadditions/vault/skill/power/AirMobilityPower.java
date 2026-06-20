package io.github.a1qs.vaultadditions.vault.skill.power;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.client.vault.ClientPowerData;
import io.github.a1qs.vaultadditions.events.client.KeybindEvents;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT,  modid = VaultAdditions.MOD_ID)
public class AirMobilityPower extends LearnableSkill {
    private float playerBaseSpeed;
    private float playerBaseAirMovement;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAirMobilityTick(TickEvent.PlayerTickEvent event) {
        if (KeybindEvents.isZephyrToggled) {
            return;
        }

        PowerTree tree = ClientPowerData.getPowerTree();
        float playerBaseSpeed = 0.0F;
        float playerBaseAirMovement = 0.0F;
        for (AirMobilityPower power : tree.getAll(AirMobilityPower.class, Skill::isUnlocked)) {
            playerBaseSpeed = Math.max(playerBaseSpeed, power.playerBaseSpeed);
            playerBaseAirMovement = Math.max(playerBaseAirMovement, power.playerBaseAirMovement);
        }

        AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(event.player);
        playerBaseSpeed = Math.max(playerBaseSpeed, snapshot.getAttributeValue(ModGearAttributes.AIR_MOBILITY_SPEED, VaultGearAttributeTypeMerger.floatMax()));
        playerBaseAirMovement = Math.max(playerBaseAirMovement, snapshot.getAttributeValue(ModGearAttributes.AIR_MOBILITY_CONTROL, VaultGearAttributeTypeMerger.floatMax()));

        for (Pair<Float, Float> value : snapshot.getAttributeValueList(ModGearAttributes.AIR_MOBILITY)) {
            playerBaseSpeed = Math.max(playerBaseSpeed, value.getFirst());
            playerBaseAirMovement = Math.max(playerBaseAirMovement, value.getSecond());
        }

        if (playerBaseSpeed > 0.0F) {
            event.player.setSpeed(playerBaseSpeed);
        }

        if (playerBaseAirMovement > 0.0F) {
            event.player.flyingSpeed = playerBaseAirMovement;
        }
    }

    public void writeBits(BitBuffer buffer) {
        super.writeBits(buffer);
        buffer.writeFloat(this.playerBaseSpeed);
        buffer.writeFloat(this.playerBaseAirMovement);
    }

    public void readBits(BitBuffer buffer) {
        super.readBits(buffer);
        this.playerBaseSpeed = Adapters.FLOAT.readBits(buffer).orElse(0F);
        this.playerBaseAirMovement = Adapters.FLOAT.readBits(buffer).orElse(0F);
    }

    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            nbt.putFloat("playerBaseSpeed", this.playerBaseSpeed);
            nbt.putFloat("playerBaseAirMovement", this.playerBaseAirMovement);
            return nbt;
        });
    }

    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.playerBaseSpeed = Adapters.FLOAT.readNbt(nbt.get("playerBaseSpeed")).orElse(0F);
        this.playerBaseAirMovement = Adapters.FLOAT.readNbt(nbt.get("playerBaseAirMovement")).orElse(0F);
    }

    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            json.addProperty("playerBaseSpeed", this.playerBaseSpeed);
            json.addProperty("playerBaseAirMovement", this.playerBaseAirMovement);
            return json;
        });
    }

    public void readJson(JsonObject json) {
        super.readJson(json);
        this.playerBaseSpeed = Adapters.FLOAT.readJson(json.get("playerBaseSpeed")).orElse(0F);
        this.playerBaseAirMovement = Adapters.FLOAT.readJson(json.get("playerBaseAirMovement")).orElse(0F);
    }

}
