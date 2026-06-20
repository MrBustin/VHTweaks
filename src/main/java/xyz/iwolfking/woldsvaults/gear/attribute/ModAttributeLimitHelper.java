package xyz.iwolfking.woldsvaults.gear.attribute;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import xyz.iwolfking.woldsvaults.init.ModGearAttributes;

public class ModAttributeLimitHelper {
    public static float getManaCostReductionLimit(LivingEntity e) {
        float limit = 0.50F;
        AttributeSnapshot snap = AttributeSnapshotHelper.getInstance().getSnapshot(e);
        limit += snap.getAttributeValue(ModGearAttributes.MANA_COST_REDUCTION_CAP, VaultGearAttributeTypeMerger.floatSum());
        return Mth.clamp(limit, 0.0F, 0.95F);
    }

    public static float getEchoingDamageLimit(LivingEntity e) {
        float limit = 0.50F;
        AttributeSnapshot snap = AttributeSnapshotHelper.getInstance().getSnapshot(e);
        limit += snap.getAttributeValue(ModGearAttributes.ECHOING_DAMAGE_CAP, VaultGearAttributeTypeMerger.floatSum());
        return Mth.clamp(limit, 0.0F, 0.95F);
    }

}
