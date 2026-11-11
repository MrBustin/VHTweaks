package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.DeckSocketItem;
import iskallia.vault.item.gear.RecyclableItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.Locale;
import java.util.UUID;

@Mixin(value = DeckSocketItem.class, remap = false)
public abstract class MixinDeckSocketItem implements RecyclableItem {
    // Like CardItem, use a shared processing ID without adding NBT to stackable items.
    @Unique
    private static final UUID woldsvaults$recyclingId = UUID.randomUUID();

    @Override
    public boolean isValidInput(ItemStack stack) {
        return true;
    }

    @Override
    public float getResultPercentage(ItemStack stack) {
        return 1.0F;
    }

    @Override
    public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack stack) {
        // DeckSocketItem.initialize removes the temporary top-level ModifierRoll after
        // applying it. The durable tier is stored in DeckModifier.config.selectedRoll.
        String tier = "";
        if (stack.hasTag() && stack.getTag().contains("DeckModifier")) {
            net.minecraft.nbt.CompoundTag modifierTag = stack.getTag().getCompound("DeckModifier");
            net.minecraft.nbt.CompoundTag configTag = modifierTag.contains("config")
                    ? modifierTag.getCompound("config") : modifierTag;
            tier = configTag.getString("selectedRoll");
        }
        // Handle uninitialized cores that have not yet had their temporary roll applied.
        if (tier.isBlank() && stack.hasTag()) {
            tier = stack.getTag().getString("ModifierRoll");
        }
        if (tier.isBlank()) {
            tier = DeckSocketItem.getDeckModifier(stack)
                    .map(modifier -> modifier.getConfig().getSelectedRollId())
                    .orElse("");
        }
        tier = tier.toLowerCase(Locale.ROOT);

        if (tier.equals("lesser") || tier.equals("greater")) {
            Optional<VaultRecyclerConfig.RecyclerOutput> output = ModConfigs.VAULT_RECYCLER
                    .getRecyclingOutput(new ResourceLocation("the_vault", "deck_socket_" + tier));
            if (output.isPresent()) {
                return output.get();
            }
        }

        // Call the config directly: Mixin cannot safely rebind the interface-super call.
        return ModConfigs.VAULT_RECYCLER.getRecyclingOutputOrDefault(stack.getItem());
    }

    @Override
    public Optional<UUID> getUuid(ItemStack stack) {
        return Optional.of(woldsvaults$recyclingId);
    }
}
