package xyz.iwolfking.woldsvaults.modifiers.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MythicalModifier extends VaultModifier<MythicalModifier.Properties> {
    private static final List<Item> GEAR_POOL = List.of(
            iskallia.vault.init.ModItems.SWORD,
            iskallia.vault.init.ModItems.AXE,
            iskallia.vault.init.ModItems.HELMET,
            iskallia.vault.init.ModItems.CHESTPLATE,
            iskallia.vault.init.ModItems.LEGGINGS,
            iskallia.vault.init.ModItems.BOOTS,
            iskallia.vault.init.ModItems.SHIELD,
            iskallia.vault.init.ModItems.WAND,
            iskallia.vault.init.ModItems.FOCUS
    );

    public MythicalModifier(ResourceLocation id, Properties properties, Display display) {
        super(id, properties, display);
        this.setDescriptionFormatter((description, modifierProperties, stackSize) ->
                description.formatted(modifierProperties.getChance() * stackSize * 100.0F));
    }

    @Override
    public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
        CommonEvents.CHEST_LOOT_GENERATION.post().register(context.getUUID(), data -> {
            if (data.getPlayer().level != world) {
                return;
            }

            List<ModifierContext> applicableContexts = vault.get(Vault.MODIFIERS)
                    .getContexts(this)
                    .filter(modifierContext -> !modifierContext.hasTarget()
                            || modifierContext.getTarget().equals(data.getPlayer().getUUID()))
                    .toList();
            UUID primaryContext = applicableContexts.stream()
                    .map(ModifierContext::getUUID)
                    .min(UUID::compareTo)
                    .orElse(null);
            if (!context.getUUID().equals(primaryContext)) {
                return;
            }

            Block block = data.getState().getBlock();
            if (block != ModBlocks.ORNATE_CHEST && block != ModBlocks.ORNATE_STRONGBOX && block != ModBlocks.ORNATE_BARREL) {
                return;
            }

            List<Integer> gearIndices = new ArrayList<>();
            for (int i = 0; i < data.getLoot().size(); i++) {
                if (GEAR_POOL.contains(data.getLoot().get(i).getItem())) {
                    gearIndices.add(i);
                }
            }
            if (gearIndices.isEmpty()) {
                return;
            }

            float chance = Math.min(1.0F, this.properties.getChance() * applicableContexts.size());
            if (data.getRandom().nextFloat() >= chance) {
                return;
            }

            int lootIndex = gearIndices.get(data.getRandom().nextInt(gearIndices.size()));
            Item gearItem = data.getLoot().get(lootIndex).getItem();
            ItemStack stack = new ItemStack(gearItem);
            VaultGearData gearData = VaultGearData.read(stack);
            gearData.setItemLevel(vault.get(Vault.LEVEL).get());
            gearData.setRarity(VaultGearRarity.valueOf("MYTHIC"));
            gearData.setState(VaultGearState.UNIDENTIFIED);
            gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, "Mythic");
            gearData.createOrReplaceAttributeValue(ModGearAttributes.IS_LOOT, true);
            gearData.write(stack);
            data.getLoot().set(lootIndex, stack);
        });
    }

    public static class Properties {
        @Expose
        private final float chance;

        public Properties(float chance) {
            this.chance = chance;
        }

        public float getChance() {
            return this.chance;
        }
    }
}
