package xyz.iwolfking.woldsvaults.events.overworld;


import com.mojang.datafixers.util.Pair;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;
import xyz.iwolfking.woldsvaults.world.overworld.PortalSavedData;
import xyz.iwolfking.woldsvaults.world.overworld.StructureSize;
import xyz.iwolfking.woldsvaults.world.overworld.structure.OverworldVaultStructures;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StructureTrackingEventHandler {

    private static final List<ResourceLocation> validStructures = new ArrayList<>(
            Arrays.asList(
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal1_leafy_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal1_desert_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal1_mesa_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal1_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_desert_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_leafy_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_mesa_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_end_ruin0"),
                    new ResourceLocation(WoldsVaults.MOD_ID, "portal2_nether_ruin0")
            ));

    private static final Map<ResourceLocation, Pair<StructureSize, Map<Rotation, BlockPos>>> structureRotationOffsets = Map.of(
            new ResourceLocation("woldsvaults:portal2_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal2_leafy_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal1_ruin0"), new Pair<>(StructureSize.LARGE, Map.of(
                    Rotation.NONE, new BlockPos(-1, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal2_desert_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal1_desert_ruin0"), new Pair<>(StructureSize.LARGE, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal2_mesa_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal1_mesa_ruin0"), new Pair<>(StructureSize.LARGE, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_180, new BlockPos(-1, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal2_end_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal2_nether_ruin0"), new Pair<>(StructureSize.SMALL, Map.of(
                    Rotation.NONE, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, 0),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            )),
            new ResourceLocation("woldsvaults:portal1_leafy_ruin0"), new Pair<>(StructureSize.LARGE, Map.of(
                    Rotation.NONE, new BlockPos(-1, -1, -1),
                    Rotation.CLOCKWISE_90, new BlockPos(0, -1, -1),
                    Rotation.CLOCKWISE_180, new BlockPos(0, -1, 0),
                    Rotation.COUNTERCLOCKWISE_90, new BlockPos(-1, -1, 0)
            ))
    );

    @SubscribeEvent
    public static void onWorldLoad(ChunkEvent.Load event) {
        if (event.getChunk() instanceof LevelChunk chunk) {
            if(chunk.getLevel().isClientSide()) return;
            if(chunk.getLevel() instanceof VirtualWorld) return;

            if(chunk.getLevel() instanceof ServerLevel level) {
                Map<ConfiguredStructureFeature<?, ?>, StructureStart> structureStarts = chunk.getAllStarts();

                for (Map.Entry<ConfiguredStructureFeature<?, ?>, StructureStart> entry : structureStarts.entrySet()) {
                    ConfiguredStructureFeature<?, ?> structureFeature = entry.getKey();
                    StructureStart structureStart = entry.getValue();

                    if (structureFeature.feature == OverworldVaultStructures.VAULT_PORTAL_STRUCTURES.get() || structureFeature.feature == OverworldVaultStructures.NETHER_VAULT_PORTAL_STRUCTURES.get()) {
                        trackStructurePositions(structureStart, level);
                    }
                }
            }
        }
    }

    private static void trackStructurePositions(StructureStart structureStart, ServerLevel level) {
        for (StructurePiece piece : structureStart.getPieces()) {
            BlockPos pos = piece.getBoundingBox().getCenter();

            if (piece instanceof PoolElementStructurePiece poolPiece) {
                if (poolPiece.getElement() instanceof SinglePoolElement singleElement) {
                    if(singleElement.template.left().isPresent()) {
                        ResourceLocation templateName = singleElement.template.left().get();
                        if(validStructures.contains(templateName)) {
                            Rotation rotation = poolPiece.getRotation();
                            BlockPos portalFramePos = pos.offset(structureRotationOffsets.get(templateName).getSecond().get(rotation));


                            PortalData portalData = new PortalData(rotation, portalFramePos, structureRotationOffsets.get(templateName).getFirst(), level.dimension(), false, -1);
                            PortalSavedData savedData = PortalSavedData.get(level);
                            savedData.addPortalData(portalData);
                            //size.getBlockPositions(portalFramePos, rotation).forEach(position -> System.out.println("Block at: " + position));

                        }
                    }
                }
            }
        }
    }

}
