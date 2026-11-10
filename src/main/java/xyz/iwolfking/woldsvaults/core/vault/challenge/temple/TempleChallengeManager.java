package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import iskallia.vault.VaultMod;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.TempleControllerProxyBlockEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.curse.renderer.context.ChallengeRenderContext;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.storage.WorldZone;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldZonesData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

import static iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity.State.*;
import static iskallia.vault.core.event.CommonEvents.BLOCK_USE;
import static iskallia.vault.core.event.common.BlockUseEvent.Phase.HEAD;
import static iskallia.vault.core.vault.Vault.SEED;

public class TempleChallengeManager extends ChallengeManager implements TempleAnimationHolder {

    public static void registerAdapter() {
        Adapters.CHALLENGE_MANAGER.register("woldsvaults:temple", TempleChallengeManager.class, TempleChallengeManager::new);
        TempleActions.registerAdapters();
    }

    @Getter
    private final TempleAnimation animation = new TempleAnimation();

    @Override
    public TempleAnimation woldsvaults$getAnimation() {
        return this.animation;
    }

    public static final ResourceLocation HUD = xyz.iwolfking.woldsvaults.WoldsVaults.id("textures/gui/challenge/temple/hud.png");
    private static final int INFUSION_TICKS = 20 * 3;
    public static final EnumAdapter<Phase> PHASE = Adapters.ofEnum(Phase.class, EnumAdapter.Mode.NAME);

    @Getter @Setter
    private BlockCuboid zone;

    @Setter
    private ChallengeAction<?> positivePool;
    @Setter
    private ChallengeAction<?> negativePool;

    @Getter
    private final List<BlockPos> proxyPositions = new ArrayList<>();

    @Getter
    private final List<BlockPos> spawnPositions = new ArrayList<>();

    @Getter @Setter
    private Phase phase = Phase.IDLE;

    @Getter
    private final TempleSpawner spawner = new TempleSpawner();

    @Getter
    private final List<TempleCorner> corners = new ArrayList<>();

    private final List<ChallengeAction<?>> selectedPositives = new ArrayList<>();
    private final List<ChallengeAction<?>> selectedNegatives = new ArrayList<>();

    private boolean scannedForSpawnPositions = false;
    private int infusingTicks = 0;

    public TempleChallengeManager() {}

    public TempleChallengeManager(UUID uuid, ResourceKey<Level> dimension, BlockPos pos,
                                  BlockCuboid zone, ChallengeAction<?> positivePool,
                                  ChallengeAction<?> negativePool) {
        super(uuid, dimension, pos);
        this.zone = zone;
        this.positivePool = positivePool;
        this.negativePool = negativePool;
    }

    @Override
    public void onAttach(ServerLevel world) {
        super.onAttach(world);
        this.spawner.onAttach(world, this);

        BLOCK_USE.in(world).at(HEAD).of(ModBlocks.TEMPLE_CONTROLLER_PROXY).register(this, data -> {
            if (data.getHand() != InteractionHand.MAIN_HAND) {
                data.setResult(InteractionResult.SUCCESS);
                return;
            }

            if (this.phase != Phase.PROXY_SELECTION) return;

            BlockPos offset = data.getPos().subtract(this.pos);
            if (!this.proxyPositions.contains(offset)) return;

            if (data.getPlayer() instanceof ServerPlayer player) {
                this.onProxyClick(world, player, data.getPos());
            }
        });
    }

    @Override
    public void onClick(ServerLevel world, Player player) {
        ChallengeControllerBlockEntity<?> controller = this.getController(world.getServer());
        if (controller == null) return;

        if (controller.getState() == ACTIVE && this.phase == Phase.PROXY_SELECTION) {
            if (this.selectedPositives.isEmpty() && this.corners.stream().noneMatch(TempleCorner::isSelected)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(
                            new TextComponent("You must infuse at least one pillar first!").withStyle(ChatFormatting.RED), true);
                }
                return;
            }
            this.destroyAllProxies(world);
            this.phase = Phase.ACTIVATING;
            this.animation.onStart(TempleAnimation.State.CLOSE_ROOM);
            this.infusingTicks = 0;
            world.playSound(null, this.pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 2.0F);
        }
    }

    @Override
    public void onTick(ServerLevel world) {
        if (this.isDeleted()) return;

        this.animation.onTick(world, this.pos);

        if (!this.scannedForSpawnPositions && this.zone != null) {
            this.scanForSpawnPositions(world);
        }

        if (this.zoneId < 0 && this.zone != null) {
            this.zoneId = WorldZonesData.get(world.getServer()).getOrCreate(world.dimension())
                    .add(new WorldZone().add(this.zone.offset(this.pos)).setModify(false));
        }

        if (this.phase == Phase.IDLE) {
            this.phase = Phase.PROXY_SELECTION;
            this.setControllerState(world, ACTIVE);
            this.spawnProxies(world);
        }

        if (this.phase == Phase.PROXY_SELECTION && world.getGameTime() % 20 == 0) {
            this.refreshProxyRewards(world);
        }

        switch (this.phase) {
            case ACTIVATING -> {
                this.infusingTicks++;
                if (this.infusingTicks >= INFUSION_TICKS && this.animation.isCompleted()) {
                    this.activateChallenge(world);
                }
            }
            case COMBAT -> {
                this.spawner.onTick(world);
                if (this.spawner.isCompleted()) {
                    this.beginReveal(world);
                }
            }
            case REVEALING -> {
                if (this.activatePositives(world)) this.completeChallenge(world);
            }
            case COMPLETED -> {
                if (!this.animation.isCompleted()) break;
                this.setControllerState(world, DESTROYED);
                this.invokeCompletionEvent(world);
                this.setDeleted(true);
            }
            default -> {}
        }

        this.refreshPlayers(world);
        super.onTick(world);
    }

    private void scanForSpawnPositions(ServerLevel world) {
        this.scannedForSpawnPositions = true;
        BlockCuboid worldZone = this.zone.offset(this.pos);

        IZonedWorld.runWithBypass(world, true, () -> {
            for (int x = worldZone.getMinX(); x <= worldZone.getMaxX(); x++) {
                for (int y = worldZone.getMinY(); y <= worldZone.getMaxY(); y++) {
                    for (int z = worldZone.getMinZ(); z <= worldZone.getMaxZ(); z++) {
                        BlockPos worldPos = new BlockPos(x, y, z);
                        BlockState state = world.getBlockState(worldPos);

                        if (state.is(iskallia.vault.init.ModBlocks.PLACEHOLDER)
                                && state.getValue(PlaceholderBlock.TYPE) == PlaceholderBlock.Type.SPAWN_POSITION) {
                            BlockPos offset = worldPos.subtract(this.pos);
                            this.spawnPositions.add(offset);
                            world.removeBlock(worldPos, false);
                        }
                    }
                }
            }
        });
    }

    private void spawnProxies(ServerLevel world) {
        ChunkRandom random = ChunkRandom.any();
        random.setDecoratorSeed(ServerVaults.get(world).map(vault -> vault.get(SEED)).orElse(0L),
                this.pos.getX(), this.pos.getZ(), 193847561);

        int vaultLevel = this.getVaultLevel(world);

        for (BlockPos offset : this.proxyPositions) {
            BlockPos proxyPos = this.pos.offset(offset);

            IZonedWorld.runWithBypass(world, true, () ->
                    world.setBlockAndUpdate(proxyPos, ModBlocks.TEMPLE_CONTROLLER_PROXY.defaultBlockState()));

            if (world.getBlockEntity(proxyPos) instanceof TempleControllerProxyBlockEntity proxy) {
                proxy.setController(this.pos);

                TempleCorner corner = this.cornerFor(offset);
                ChallengeAction<?> positive = corner == null ? rollAction(this.positivePool, random, vaultLevel) : null;
                if (corner != null) proxy.setRewardDescription(corner.describe(world, this.pos));
                ChallengeAction<?> negative = rollAction(this.negativePool, random, vaultLevel);

                proxy.setPositiveAction(positive);
                proxy.setNegativeAction(negative);
            }
        }

        world.playSound(null, this.pos, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.BLOCKS, 1.4F, 1.0F);
    }

    private ChallengeAction<?> rollAction(ChallengeAction<?> action, RandomSource random, int vaultLevel) {
        if (action == null) return null;

        return action.copy().flatten(random, vaultLevel)
                .map(TempleActions::localize)
                .peek(a -> a.onPopulate(random))
                .findFirst()
                .orElse(null);
    }

    private void onProxyClick(ServerLevel world, ServerPlayer player, BlockPos proxyPos) {
        if (!(world.getBlockEntity(proxyPos) instanceof TempleControllerProxyBlockEntity proxy)) return;

        if (!this.pos.equals(proxy.getController())) return;
        TempleCorner corner = this.cornerFor(proxyPos.subtract(this.pos));
        if (corner != null) {
            if (corner.isSelected()) return;
            corner.select();
        }
        ChallengeAction<?> positive = corner == null ? TempleActions.localize(proxy.getPositiveAction()) : null;
        ChallengeAction<?> negative = TempleActions.localize(proxy.getNegativeAction());

        if (positive != null) this.selectedPositives.add(positive);
        if (negative != null) this.selectedNegatives.add(negative);

        IZonedWorld.runWithBypass(world, true, () -> world.destroyBlock(proxyPos, false));

        ModNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(proxyPos)),
                new AbsorbingParticleMessage(
                        new Vec3(proxyPos.getX() + 0.5D, proxyPos.getY(), proxyPos.getZ() + 0.5D),
                        new Vec3(this.pos.getX() + 0.5D, this.pos.getY() + 1.65D, this.pos.getZ() + 0.5D),
                        0x4ae268
                )
        );

        world.playSound(null, this.pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 2.0F);
    }

    private TempleCorner cornerFor(BlockPos proxyOffset) {
        return this.corners.stream().filter(corner -> corner.proxy.equals(proxyOffset)).findFirst().orElse(null);
    }

    private void refreshProxyRewards(ServerLevel world) {
        for (TempleCorner corner : this.corners) {
            if (!corner.isSelected() && world.getBlockEntity(this.pos.offset(corner.proxy)) instanceof TempleControllerProxyBlockEntity proxy) {
                proxy.setRewardDescription(corner.describe(world, this.pos));
            }
        }
    }

    private void activateChallenge(ServerLevel world) {
        RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());

        for (ChallengeAction<?> negative : this.selectedNegatives) {
            negative.onActivate(world, this, random);
        }

        if (this.spawner.hasSpawns()) {
            this.spawner.spawn(world, this.pos, this.spawnPositions);
            this.phase = Phase.COMBAT;
            world.playSound(null, this.pos, ModSounds.PUZZLE_COMPLETION_MINOR, SoundSource.MASTER, 1.0F, 0.7F);
        } else {
            this.beginReveal(world);
        }
    }

    private void beginReveal(ServerLevel world) {
        this.phase = Phase.REVEALING;
        if (this.activatePositives(world)) this.completeChallenge(world);
    }

    private boolean activatePositives(ServerLevel world) {
        boolean revealed = true;
        for (TempleCorner corner : this.corners) revealed &= corner.reveal(world, this.pos);
        if (!revealed) return false;
        RandomSource random = JavaRandom.ofInternal(world.getRandom().nextLong());
        for (ChallengeAction<?> positive : this.selectedPositives) {
            positive.onActivate(world, this, random);
        }
        return true;
    }

    private void completeChallenge(ServerLevel world) {
        this.phase = Phase.COMPLETED;
        this.animation.onStart(TempleAnimation.State.OPEN_ROOM);
        world.playSound(null, this.pos, ModSounds.PUZZLE_COMPLETION_MAJOR, SoundSource.MASTER, 1.0F, 1.0F);
    }

    private void destroyAllProxies(ServerLevel world) {
        IZonedWorld.runWithBypass(world, true, () -> {
            for (BlockPos offset : this.proxyPositions) {
                world.destroyBlock(this.pos.offset(offset), false);
            }
        });
    }

    @Override
    public void onRemove(MinecraftServer server) {
        ServerLevel level = server.getLevel(this.dimension);
        if (level != null) {
            this.destroyAllProxies(level);
            this.setControllerState(level, DESTROYED);
        }
        this.spawner.onDetach();
        super.onRemove(server);
    }

    @Override
    public void onDetach() {
        this.spawner.onDetach();
        super.onDetach();
    }

    @Override
    public boolean shouldRenderObjectives() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRender(ChallengeRenderContext context) {
        super.onRender(context);

        if (this.phase == Phase.IDLE) return;

        if (this.phase == Phase.PROXY_SELECTION) {
            MutableComponent text = new TextComponent("Infuse the controller with proxies!").withStyle(ChatFormatting.GOLD);
            this.renderProgressBar(context, HUD, 1.0D, text);
        } else if (this.phase == Phase.COMBAT) {
            double progress = this.spawner.getTotalHealth() > 0
                    ? this.spawner.getCurrentHealth() / this.spawner.getTotalHealth()
                    : 1.0D;

            int mobsRemaining = (int) Math.ceil(this.spawner.getCurrentHealth());
            MutableComponent text = new TextComponent("Defeat all enemies!").withStyle(ChatFormatting.WHITE);
            this.renderProgressBar(context, HUD, progress, text);
        }
    }

    @Override
    public Optional<CompoundTag> writeNbt() {
        return super.writeNbt().map(nbt -> {
            this.animation.writeNbt(nbt);
            ListTag cornersTag = new ListTag();
            this.corners.forEach(corner -> cornersTag.add(corner.writeNbt()));
            nbt.put("corners", cornersTag);
            Adapters.BLOCK_CUBOID.writeNbt(this.zone).ifPresent(tag -> nbt.put("zone", tag));
            PHASE.asNullable().writeNbt(this.phase).ifPresent(tag -> nbt.put("phase", tag));
            Adapters.BOOLEAN.writeNbt(this.scannedForSpawnPositions).ifPresent(tag -> nbt.put("scannedForSpawnPositions", tag));
            Adapters.INT.writeNbt(this.infusingTicks).ifPresent(tag -> nbt.put("infusingTicks", tag));

            Adapters.CHALLENGE_ACTION.writeNbt(this.positivePool).ifPresent(tag -> nbt.put("positivePool", tag));
            Adapters.CHALLENGE_ACTION.writeNbt(this.negativePool).ifPresent(tag -> nbt.put("negativePool", tag));

            ListTag proxyPosTag = new ListTag();
            this.proxyPositions.forEach(p -> Adapters.BLOCK_POS.writeNbt(p).ifPresent(proxyPosTag::add));
            nbt.put("proxyPositions", proxyPosTag);

            ListTag spawnPosTag = new ListTag();
            this.spawnPositions.forEach(p -> Adapters.BLOCK_POS.writeNbt(p).ifPresent(spawnPosTag::add));
            nbt.put("spawnPositions", spawnPosTag);

            this.writeActionsNbt(nbt, "selectedPositives", this.selectedPositives);
            this.writeActionsNbt(nbt, "selectedNegatives", this.selectedNegatives);

            this.spawner.writeNbt().ifPresent(tag -> nbt.put("spawner", tag));

            return nbt;
        });
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        super.readNbt(nbt);
        this.animation.readNbt(nbt);
        this.corners.clear();
        for (Tag entry : nbt.getList("corners", Tag.TAG_COMPOUND)) this.corners.add(TempleCorner.readNbt((CompoundTag) entry));
        this.zone = Adapters.BLOCK_CUBOID.readNbt(nbt.get("zone")).orElse(null);
        this.phase = PHASE.readNbt(nbt.get("phase")).orElse(Phase.IDLE);
        this.scannedForSpawnPositions = Adapters.BOOLEAN.readNbt(nbt.get("scannedForSpawnPositions")).orElse(false);
        this.infusingTicks = Adapters.INT.readNbt(nbt.get("infusingTicks")).orElse(0);

        this.positivePool = Adapters.CHALLENGE_ACTION.readNbt(nbt.get("positivePool")).orElse(null);
        this.negativePool = Adapters.CHALLENGE_ACTION.readNbt(nbt.get("negativePool")).orElse(null);

        this.proxyPositions.clear();
        if (nbt.get("proxyPositions") instanceof ListTag list) {
            for (Tag tag : list) {
                Adapters.BLOCK_POS.readNbt(tag).ifPresent(this.proxyPositions::add);
            }
        }

        this.spawnPositions.clear();
        if (nbt.get("spawnPositions") instanceof ListTag list) {
            for (Tag tag : list) {
                Adapters.BLOCK_POS.readNbt(tag).ifPresent(this.spawnPositions::add);
            }
        }

        this.readActionsNbt(nbt, "selectedPositives", this.selectedPositives);
        this.readActionsNbt(nbt, "selectedNegatives", this.selectedNegatives);
        this.selectedPositives.replaceAll(TempleActions::localize);
        this.selectedNegatives.replaceAll(TempleActions::localize);

        if (!nbt.contains("gateAnimationState")) {
            if (this.phase == Phase.ACTIVATING) this.animation.onStart(TempleAnimation.State.CLOSE_ROOM);
            if (this.phase == Phase.COMPLETED) this.animation.onStart(TempleAnimation.State.OPEN_ROOM);
        }

        if (nbt.contains("spawner")) {
            this.spawner.readNbt(nbt.getCompound("spawner"));
        }
    }

    public enum Phase {
        IDLE,
        PROXY_SELECTION,
        ACTIVATING,
        COMBAT,
        REVEALING,
        COMPLETED
    }
}
