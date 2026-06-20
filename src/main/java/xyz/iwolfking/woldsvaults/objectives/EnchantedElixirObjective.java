package xyz.iwolfking.woldsvaults.objectives;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.ElixirObjective;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.elixir.ElixirGoal;
import iskallia.vault.core.vault.objective.elixir.ElixirTask;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.api.core.vault_events.lib.EventTag;
import xyz.iwolfking.woldsvaults.api.util.ObjectiveHelper;
import xyz.iwolfking.woldsvaults.api.util.VaultModifierUtils;
import xyz.iwolfking.woldsvaults.init.ModConfigs;
import xyz.iwolfking.woldsvaults.modifiers.vault.EnchantedElixirEventAmountModifier;
import xyz.iwolfking.woldsvaults.modifiers.vault.EnchantedElixirPoolModifier;
import xyz.iwolfking.woldsvaults.objectives.data.EnchantedEventsRegistry;
import xyz.iwolfking.woldsvaults.objectives.enchanted_elixir.ElixirBreakpointMap;
import xyz.iwolfking.woldsvaults.objectives.enchanted_elixir.ElixirCollectionMap;
import xyz.iwolfking.woldsvaults.objectives.lib.BasicEnchantedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantedElixirObjective extends ElixirObjective {
    public static final FieldKey<ElixirBreakpointMap> ELIXIR_BREAKPOINTS = FieldKey.of("elixir_breakpoints", ElixirBreakpointMap.class).with(Version.v1_0, CompoundAdapter.of(ElixirBreakpointMap::new), DISK.all().or(CLIENT.all())).register(FIELDS);
    public static final FieldKey<ElixirCollectionMap> ELIXIR_COLLECTION = FieldKey.of("elixir_collection", ElixirCollectionMap.class).with(Version.v1_0, CompoundAdapter.of(ElixirCollectionMap::new), DISK.all().or(CLIENT.all())).register(FIELDS);
    public static final SupplierKey<Objective> E_KEY = SupplierKey.of("enchanted_elixir", Objective.class).with(Version.v1_12, EnchantedElixirObjective::new);

    private final List<Listener> completedListeners = new ArrayList<>();
    private float cascadeIncrease = 0.0F;
    private boolean shouldCascadeRandomly = false;
    private boolean shouldCascade = true;

    protected EnchantedElixirObjective() {
        this.set(GOALS, new GoalMap());
        this.set(ELIXIR_BREAKPOINTS, new ElixirBreakpointMap());
        this.set(ELIXIR_COLLECTION, new ElixirCollectionMap());
    }

    public static EnchantedElixirObjective create() {
        return new EnchantedElixirObjective();
    }

    @Override
    public SupplierKey<Objective> getKey() {
        return E_KEY;
    }

    @Override
    public void initServer(VirtualWorld world, Vault vault) {
        ObjectiveHelper.handleAddingNormalizedToVault(vault, world);
        super.initServer(world, vault);
    }

    @Override
    public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
        if (world == null || vault == null || listener == null) {
            return;
        }

        if (listener instanceof Runner runner && listener.getPriority(this) < 0) {
            listener.addObjective(vault, this);
            this.generateGoal(world, vault, runner);
            listener.getPlayer().ifPresent(player -> {
                this.get(ELIXIR_COLLECTION).put(player, 0);
                this.generateElixirBreakpointsMap(listener, true, vault);
            });
        }

        ServerPlayer listenerPlayer = listener.getPlayer().orElse(null);
        if (listenerPlayer == null) {
            return;
        }

        ElixirGoal goal = this.get(GOALS).get(listener.get(Listener.ID));
        if (goal == null) {
            return;
        }

        if (this.get(ELIXIR_BREAKPOINTS).get(listenerPlayer) == null) {
            this.generateElixirBreakpointsMap(listener, false, vault);
            Integer currentElixir = goal.get(ElixirGoal.CURRENT);
            List<Float> breakpoints = this.get(ELIXIR_BREAKPOINTS).get(listenerPlayer);
            for (int i = 0; breakpoints != null && i < breakpoints.size() - 1; i++) {
                if (breakpoints.get(i) >= currentElixir) {
                    if (i + 1 >= breakpoints.size()) {
                        completedListeners.add(listener);
                    }
                    this.get(ELIXIR_COLLECTION).put(listenerPlayer, i);
                    break;
                }
            }
        }

        if (!goal.isCompleted()) {
            List<Float> breakpoints = this.get(ELIXIR_BREAKPOINTS).get(listenerPlayer);
            Integer collectionIndex = this.get(ELIXIR_COLLECTION).get(listenerPlayer);
            if (breakpoints != null && collectionIndex != null && collectionIndex < breakpoints.size()
                    && goal.get(ElixirGoal.CURRENT) >= breakpoints.get(collectionIndex)) {
                this.get(ELIXIR_COLLECTION).put(listenerPlayer, collectionIndex + 1);
                this.triggerRandomEvent(listenerPlayer, vault);
            }
        }

        if (goal.isCompleted() && !completedListeners.contains(listener)) {
            completedListeners.add(listener);
            this.triggerOmegaRandomEvent(listenerPlayer, vault);
        }

        if (goal.isCompleted()) {
            this.get(CHILDREN).forEach(child -> child.tickListener(world, vault, listener));
        }
    }

    private void generateGoal(VirtualWorld world, Vault vault, Runner listener) {
        ElixirGoal goal = new ElixirGoal();
        this.get(GOALS).put(listener.get(Listener.ID), goal);
        JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED) ^ listener.get(Listener.ID).getMostSignificantBits());
        goal.set(ElixirGoal.TARGET, ModConfigs.ENCHANTED_ELIXIR.generateTarget(vault.get(Vault.LEVEL).get(), random));
        goal.set(ElixirGoal.BASE_TARGET, goal.get(ElixirGoal.TARGET));

        for (ElixirTask task : ModConfigs.ENCHANTED_ELIXIR.generateGoals(vault.get(Vault.LEVEL).get(), random)) {
            goal.get(ElixirGoal.TASKS).add(task);
        }

        goal.initServer(world, vault, this, listener.getId());
    }

    private void generateElixirBreakpointsMap(Listener listener, boolean sendMessage, Vault vault) {
        int numberOfBreakpoints = new Random().nextInt(10, 25);

        EnchantedElixirEventAmountModifier amountModifier = VaultModifierUtils.getModifiersOfType(vault, EnchantedElixirEventAmountModifier.class).stream().findFirst().orElse(null);
        if (amountModifier != null) {
            numberOfBreakpoints = amountModifier.properties().getCount();
        }

        if (listener.getPlayer().isEmpty()) {
            return;
        }

        ElixirGoal goal = this.get(GOALS).get(listener.get(Listener.ID));
        if (goal == null) {
            return;
        }

        List<Float> breakpoints = new ArrayList<>();
        float elixirTarget = goal.get(ElixirGoal.TARGET).floatValue();
        for (int i = 0; i < numberOfBreakpoints; i++) {
            float decimalModifier = (i + 1.0F) / numberOfBreakpoints;
            breakpoints.add(elixirTarget * decimalModifier);
        }
        this.get(ELIXIR_BREAKPOINTS).put(listener.getPlayer().get().getUUID(), breakpoints);

        if (!sendMessage) {
            return;
        }

        if (numberOfBreakpoints >= 10 && numberOfBreakpoints < 15) {
            listener.getPlayer().ifPresent(player -> player.displayClientMessage(new TextComponent("You will experience a low number of random events this vault!").withStyle(ChatFormatting.YELLOW), false));
        } else if (numberOfBreakpoints > 14 && numberOfBreakpoints < 19) {
            listener.getPlayer().ifPresent(player -> player.displayClientMessage(new TextComponent("You will experience a moderate number of random events this vault!").withStyle(ChatFormatting.AQUA), false));
        } else if (numberOfBreakpoints > 18 && numberOfBreakpoints < 25) {
            listener.getPlayer().ifPresent(player -> player.displayClientMessage(new TextComponent("You will experience a high number of random events this vault!").withStyle(ChatFormatting.LIGHT_PURPLE), false));
        } else {
            listener.getPlayer().ifPresent(player -> player.displayClientMessage(new TextComponent("You will experience an extraordinarily high number of random events this vault!").withStyle(ChatFormatting.RED), false));
        }
    }

    private List<EventTag> getAllowedTags(Vault vault) {
        List<EventTag> allowedTags = new ArrayList<>();
        VaultModifierUtils.getModifiersOfType(vault, EnchantedElixirPoolModifier.class)
                .forEach(modifier -> allowedTags.addAll(modifier.properties().getAllowedTags()));
        return allowedTags;
    }

    private void triggerRandomEvent(ServerPlayer player, Vault vault) {
        List<EventTag> allowedTags = this.getAllowedTags(vault);
        if (allowedTags.isEmpty()) {
            EnchantedEventsRegistry.getEvents().getRandom().ifPresent(event -> event.triggerEvent(player.getOnPos(), player, vault));
            return;
        }

        EnchantedEventsRegistry.getEventsWithTags(allowedTags).getRandom().ifPresent(event -> event.triggerEvent(player.getOnPos(), player, vault));
    }

    private void triggerOmegaRandomEvent(ServerPlayer player, Vault vault) {
        List<EventTag> allowedTags = this.getAllowedTags(vault);
        if (allowedTags.isEmpty()) {
            EnchantedEventsRegistry.getOmegaEvents().getRandom().ifPresent(event -> event.triggerEvent(player.getOnPos(), player, vault));
            return;
        }

        allowedTags.add(EventTag.OMEGA);
        EnchantedEventsRegistry.getEventsWithTags(allowedTags).getRandom().ifPresent(event -> event.triggerEvent(player.getOnPos(), player, vault));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
        super.render(vault, matrixStack, window, partialTicks, player);

        ElixirGoal goal = this.get(GOALS).get(player.getUUID());
        List<Float> breakpoints = this.get(ELIXIR_BREAKPOINTS).get(player.getUUID());
        if (goal != null && breakpoints != null) {
            matrixStack.pushPose();
            matrixStack.translate(-80, 16.0, 101.0);

            float barWidth = 130.0F;
            float barOffsetX = 13.0F;
            int lineHeight = 10;
            int color = 0x80B19CD9;

            for (Float bp : breakpoints) {
                float progressPoint = bp / (float)goal.get(ElixirGoal.TARGET);
                int x = (int)(barOffsetX + (int)(barWidth * progressPoint));
                GuiComponent.fill(matrixStack, x, 0, x + 1, lineHeight, color);
            }

            matrixStack.popPose();
        }

        return true;
    }

    public void setCascadeIncrease(float value) {
        cascadeIncrease = value;
    }

    public float getCascadeIncrease() {
        return cascadeIncrease;
    }

    public void setShouldCascadeRandomly(boolean value) {
        shouldCascadeRandomly = value;
    }

    public boolean shouldCascadeRandomly() {
        return shouldCascadeRandomly;
    }

    public void setShouldCascade(boolean value) {
        shouldCascade = value;
    }

    public boolean shouldCascade() {
        return shouldCascade;
    }
}
