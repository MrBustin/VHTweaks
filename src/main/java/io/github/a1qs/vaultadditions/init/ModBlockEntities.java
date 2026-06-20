package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.block.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, VaultAdditions.MOD_ID);

    public static final RegistryObject<BlockEntityType<GlobeExpanderBlockEntity>> GLOBE_EXPANDER_ENTITY = BLOCK_ENTITIES.register("globe_expander_entity",
            () -> BlockEntityType.Builder.of(GlobeExpanderBlockEntity::new, ModBlocks.GLOBE_EXPANDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<EventBlockEntity>> EVENT_BLOCK_ENTITY = BLOCK_ENTITIES.register("event_block_entity",
            () -> BlockEntityType.Builder.of(EventBlockEntity::new, ModBlocks.EVENT_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<LootStatueBlockEntity>> LOOT_STATUE_BLOCK_ENTITY = BLOCK_ENTITIES.register("loot_statue_block_entity",
            () -> BlockEntityType.Builder.of(LootStatueBlockEntity::new,
                    ModBlocks.LOOT_STATUE_VAULT.get(),
                    ModBlocks.LOOT_STATUE_ARENA.get(),
                    ModBlocks.LOOT_STATUE_GIFT.get(),
                    ModBlocks.LOOT_STATUE_GIFT_MEGA.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<RaidPlaqueTileEntity>> RAID_PLAQUE_BLOCK_ENTITY = BLOCK_ENTITIES.register("raid_plaque_block_entity",
            () -> BlockEntityType.Builder.of(RaidPlaqueTileEntity::new, ModBlocks.RAID_PLAQUE.get()).build(null));

    public static final RegistryObject<BlockEntityType<StatueCauldronBlockEntity>> STATUE_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES.register("statue_cauldron_block_entity",
            () -> BlockEntityType.Builder.of(StatueCauldronBlockEntity::new, ModBlocks.STATUE_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<PlayerTraderBlockEntity>> PLAYER_TRADER_BLOCK_ENTITY = BLOCK_ENTITIES.register("player_trader_block_entity",
            () -> BlockEntityType.Builder.of(PlayerTraderBlockEntity::new, ModBlocks.PLAYER_TRADER.get()).build(null));

    public static final RegistryObject<BlockEntityType<EventConditionBlockEntity>> EVENT_CONDITION_BLOCK_ENTITY = BLOCK_ENTITIES.register("event_condition_block_entity",
            () -> BlockEntityType.Builder.of(EventConditionBlockEntity::new, ModBlocks.EVENT_CONDITION_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<EventActiveBlockEntity>> EVENT_ACTIVE_BLOCK_ENTITY = BLOCK_ENTITIES.register("event_active_block_entity",
            () -> BlockEntityType.Builder.of(EventActiveBlockEntity::new, ModBlocks.EVENT_ACTIVE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ColoredVelvetBedBlockEntity>> COLORED_VELVET_BED_BLOCK_ENTITY = BLOCK_ENTITIES.register("colored_velvet_bed_block_entity",
            () -> BlockEntityType.Builder.of(ColoredVelvetBedBlockEntity::new,
                    ModBlocks.VELVET_BED_WHITE.get(),
                    ModBlocks.VELVET_BED_ORANGE.get(),
                    ModBlocks.VELVET_BED_MAGENTA.get(),
                    ModBlocks.VELVET_BED_LIGHT_BLUE.get(),
                    ModBlocks.VELVET_BED_YELLOW.get(),
                    ModBlocks.VELVET_BED_LIME.get(),
                    ModBlocks.VELVET_BED_PINK.get(),
                    ModBlocks.VELVET_BED_GRAY.get(),
                    ModBlocks.VELVET_BED_LIGHT_GRAY.get(),
                    ModBlocks.VELVET_BED_CYAN.get(),
                    ModBlocks.VELVET_BED_PURPLE.get(),
                    ModBlocks.VELVET_BED_BLUE.get(),
                    ModBlocks.VELVET_BED_BROWN.get(),
                    ModBlocks.VELVET_BED_GREEN.get(),
                    ModBlocks.VELVET_BED_RED.get(),
                    ModBlocks.VELVET_BED_BLACK.get()
            ).build(null));
}
