package xyz.iwolfking.woldsvaults.util;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

import java.util.UUID;
import java.util.logging.Level;

@Mod.EventBusSubscriber(modid = "woldsvaults")
public class SneakSpeedHandler {

    private static final UUID SNEAK_SPEED_UUID = UUID.fromString("2d6b8f20-8b3e-4c6c-9b1e-7d5a4f2b9c11");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;

        if (player.level.isClientSide()) {
            return;
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }

        float sneakSpeedBonus = AttributeSnapshotHelper.getInstance()
                .getSnapshot(player)
                .getAttributeValue(ModGearAttributes.SWIFT_SNEAK, VaultGearAttributeTypeMerger.floatSum());

        movementSpeed.removeModifier(SNEAK_SPEED_UUID);

        if (player.isCrouching() && sneakSpeedBonus > 0.0f) {
            AttributeModifier modifier = new AttributeModifier(
                    SNEAK_SPEED_UUID,
                    "woldsvaultsstandalone_sneak_speed",
                    sneakSpeedBonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );

            movementSpeed.addTransientModifier(modifier);
        }
    }
}
