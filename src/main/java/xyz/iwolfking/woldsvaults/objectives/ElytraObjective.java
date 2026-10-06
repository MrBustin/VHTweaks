package xyz.iwolfking.woldsvaults.objectives;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class ElytraObjective extends Objective {
    public static final SupplierKey<Objective> KEY = SupplierKey.of("elytra", Objective.class)
            .with(Version.latest(), ElytraObjective::new);
    public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
    public static final FieldKey<UUIDList> COMPLETED_PLAYERS = FieldKey.of("completed_players", UUIDList.class)
            .with(Version.latest(), CompoundAdapter.of(UUIDList::create), DISK.all().or(CLIENT.all()))
            .register(FIELDS);
    public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
            .with(Version.latest(), Adapters.FLOAT, DISK.all()).register(FIELDS);

    protected ElytraObjective() {
        this.set(COMPLETED_PLAYERS, UUIDList.create());
    }

    private ElytraObjective(float objectiveProbability) {
        this();
        this.set(OBJECTIVE_PROBABILITY, objectiveProbability);
    }

    public static ElytraObjective of(float objectiveProbability) {
        return new ElytraObjective(objectiveProbability);
    }

    @Override
    public FieldRegistry getFields() {
        return FIELDS;
    }

    @Override
    public void initServer(VirtualWorld world, Vault vault) {
        CommonEvents.OBJECTIVE_PIECE_GENERATION.register(this, data -> {
            if (data.getVault() == vault) {
                this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability));
            }
        });
        CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD)
                .of(ModBlocks.ELYTRA_OBJECTIVE_PILLAR.get()).register(this, data -> {
                    if (!this.get(COMPLETED_PLAYERS).contains(data.getPlayer().getUUID())) {
                        this.get(COMPLETED_PLAYERS).add(data.getPlayer().getUUID());
                        world.playSound(null, data.getPos(), SoundEvents.CONDUIT_ACTIVATE,
                                SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    data.setResult(InteractionResult.SUCCESS);
                });

        this.registerObjectiveTemplate(world, vault);
        super.initServer(world, vault);
    }

    @Override
    public void tickServer(VirtualWorld world, Vault vault) {
        boolean allComplete = vault.get(Vault.LISTENERS).getAll(Runner.class).stream()
                .allMatch(runner -> this.get(COMPLETED_PLAYERS).contains(runner.getId()));
        if (allComplete) {
            super.tickServer(world, vault);
        }
    }

    @Override
    public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
        if (listener.getPriority(this) < 0) {
            listener.addObjective(vault, this);
        }
        if (this.get(COMPLETED_PLAYERS).contains(listener.getId())) {
            super.tickListener(world, vault, listener);
        }
    }

    @Override
    public boolean render(Vault vault, PoseStack poseStack, Window window, float partialTicks, Player player) {
        if (this.get(COMPLETED_PLAYERS).contains(player.getUUID())) {
            boolean rendered = false;
            for (Objective child : this.get(CHILDREN)) {
                rendered |= child.render(vault, poseStack, window, partialTicks, player);
            }
            return rendered;
        }

        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Component text = new TextComponent("Reach the objective pillar!").withStyle(ChatFormatting.LIGHT_PURPLE);
        Minecraft.getInstance().font.drawInBatch(text.getVisualOrderText(),
                -Minecraft.getInstance().font.width(text) / 2.0F, 9.0F, -1, true,
                poseStack.last().pose(), buffer, false, 0, 15728880);
        buffer.endBatch();
        return true;
    }

    @Override
    public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
        if (objective == this) {
            return true;
        }
        for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(world, vault, objective)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SupplierKey<Objective> getKey() {
        return KEY;
    }
}
