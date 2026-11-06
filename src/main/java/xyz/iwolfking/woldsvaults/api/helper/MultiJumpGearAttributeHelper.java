package xyz.iwolfking.woldsvaults.api.helper;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.world.entity.player.Player;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

public final class MultiJumpGearAttributeHelper {
    private MultiJumpGearAttributeHelper() {}

    public static int getExtraJumpCount(Player player) {
        if (player == null) {
            return 0;
        }

        return Math.max(0, AttributeSnapshotHelper.getInstance()
                .getSnapshot(player)
                .getAttributeValue(ModGearAttributes.MULTI_JUMP, VaultGearAttributeTypeMerger.intSum()));
    }
}
