package xyz.iwolfking.woldsvaults.objectives;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.KillBossObjective;
import iskallia.vault.core.vault.objective.MonolithObjective;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.*;
import iskallia.vault.item.core.DataInitializationItem;
import iskallia.vault.item.core.DataTransferItem;
import iskallia.vault.item.core.VaultLevelItem;
import iskallia.vault.network.message.MonolithIgniteMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.network.PacketDistributor;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.api.util.ObjectiveHelper;

import java.util.*;

public class HauntedBraziersObjective extends MonolithObjective {
    public static final SupplierKey<Objective> E_KEY = (SupplierKey)SupplierKey.of("haunted_braziers", Objective.class).with(Version.v1_12, HauntedBraziersObjective::new);
    public static final ResourceLocation HAUNTED_HUD = WoldsVaults.id("textures/gui/monolith/haunted_hud.png");
    public HauntedBraziersObjective() {
    }

    @Override
    public SupplierKey<Objective> getKey() {
        return E_KEY;
    }

    public HauntedBraziersObjective(int target, float objectiveProbability, ResourceLocation stackModifierPool, ResourceLocation overStackModifierPool, ResourceLocation lootTable) {
        this.set(COUNT, 0);
        this.set(TARGET, target);
        this.set(BASE_TARGET, target);
        this.set(OBJECTIVE_PROBABILITY, objectiveProbability);
        this.set(STACK_MODIFIER_POOL, stackModifierPool);
        this.set(OVER_STACK_MODIFIER_POOL, overStackModifierPool);
        this.set(OVER_STACK_LOOT_TABLE, lootTable);
    }

    public static HauntedBraziersObjective of(int target, float objectiveProbability, ResourceLocation stackModifierPool, ResourceLocation overStackModifierPool, ResourceLocation overStackLootTable) {
        return new HauntedBraziersObjective(target, objectiveProbability, stackModifierPool, overStackModifierPool, overStackLootTable);
    }


    @Override
    public void initServer(VirtualWorld world, Vault vault) {
        ObjectiveHelper.handleAddingNormalizedToVault(vault, world);
        ObjectiveHelper.addInitModifiersToVault(vault, modifiers -> {
            modifiers.add(WoldsVaults.id("ghost_town"));
            modifiers.add(VaultMod.id("haunting"));
        });

        CommonEvents.OBJECTIVE_PIECE_GENERATION.register(this, (data) -> {
            if (data.getVault() == vault) {
                this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability));
            }
        });
        this.registerObjectiveTemplate(world, vault);

        CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.MONOLITH).register(this, (data) -> {
            if (data.getHand() != InteractionHand.MAIN_HAND) {
                data.setResult(InteractionResult.SUCCESS);
            } else {
                BlockPos pos = data.getPos();
                if (data.getState().getValue(MonolithBlock.STATE) == MonolithBlock.State.EXTINGUISHED) {
                    if (((Listeners)vault.get(Vault.LISTENERS)).getObjectivePriority(data.getPlayer().getUUID(), this) == 0) {
                        boolean overStacking = (Integer)this.get(COUNT) >= (Integer)this.get(TARGET);
                        if (overStacking) {
                            world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(MonolithBlock.STATE, MonolithBlock.State.DESTROYED), 3);
                        } else {
                            world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(MonolithBlock.STATE, MonolithBlock.State.LIT), 3);
                        }

                        this.playActivationEffects(world, pos, overStacking);
                        this.set(COUNT, (Integer)this.get(COUNT) + 1);
                        if (!overStacking) {
                            Iterator var6 = ((ObjList)this.get(CHILDREN)).iterator();

                            while(var6.hasNext()) {
                                Objective objective = (Objective)var6.next();
                                if (objective instanceof KillBossObjective) {
                                    KillBossObjective killBoss = (KillBossObjective)objective;
                                    killBoss.set(KillBossObjective.BOSS_POS, pos);
                                }
                            }
                            List<Wraith> nearbyEntities = world.getEntitiesOfClass(Wraith.class, Objects.requireNonNull(world.getBlockEntity(pos)).getRenderBoundingBox().inflate(64.0D));

                            nearbyEntities.forEach(livingEntity -> {
                                livingEntity.addEffect(new MobEffectInstance(ModEffects.NO_AI, 60, 0));
                                livingEntity.hurt(DamageSource.MAGIC,livingEntity.getMaxHealth() / 2);
                            });
                        }

                        if (overStacking) {
                            LootTableKey table = (LootTableKey) VaultRegistry.LOOT_TABLE.getKey((ResourceLocation)this.get(OVER_STACK_LOOT_TABLE));
                            if (table == null) {
                                return;
                            }

                            LootTableGenerator generator = new LootTableGenerator((Version)vault.get(Vault.VERSION), table, 0.0F);
                            ChunkRandom randomx = ChunkRandom.any();
                            randomx.setBlockSeed((Long)vault.get(Vault.SEED), data.getPos(), 900397371L);
                            generator.generate(randomx);
                            List<ItemStack> loot = new ArrayList();
                            Iterator var10000 = generator.getItems();
                            Objects.requireNonNull(loot);
                            var10000.forEachRemaining((item) -> loot.add((ItemStack) item));

                            for(int i = 0; i < loot.size(); ++i) {
                                ItemStack stack = (ItemStack)loot.get(i);
                                VaultLevelItem.doInitializeVaultLoot(stack, vault, (BlockPos)null);
                                stack = DataTransferItem.doConvertStack(stack);
                                DataInitializationItem.doInitialize(stack);
                                loot.set(i, stack);
                            }

                            loot.removeIf(ItemStack::isEmpty);
                            loot.forEach((stackx) -> {
                                Block.popResource(world, pos, stackx);
                            });
                            data.setResult(InteractionResult.SUCCESS);
                        }

                        BlockEntity patt9339$temp = data.getWorld().getBlockEntity(pos);
                        if (patt9339$temp instanceof MonolithTileEntity) {
                            MonolithTileEntity tile = (MonolithTileEntity)patt9339$temp;
                            if (!tile.getModifiers().isEmpty()) {
                                Iterator<Map.Entry<ResourceLocation, Integer>> it = tile.getModifiers().entrySet().iterator();
                                TextComponent suffix = new TextComponent("");

                                while(it.hasNext()) {
                                    Map.Entry<ResourceLocation, Integer> entry = (Map.Entry)it.next();
                                    VaultModifier<?> modifier = VaultModifierRegistry.get((ResourceLocation)entry.getKey());
                                    suffix.append(modifier.getChatDisplayNameComponent((Integer)entry.getValue()));
                                    if (it.hasNext()) {
                                        suffix.append(new TextComponent(", "));
                                    }
                                }

                                TextComponent text = new TextComponent("");
                                if (!tile.getModifiers().isEmpty()) {
                                    text.append(data.getPlayer().getDisplayName()).append((new TextComponent(" added ")).withStyle(ChatFormatting.GRAY)).append(suffix).append((new TextComponent(".")).withStyle(ChatFormatting.GRAY));
                                }

                                ChunkRandom random = ChunkRandom.any();
                                random.setBlockSeed((Long)vault.get(Vault.SEED), data.getPos(), 90039737L);
                                tile.getModifiers().forEach((modifierx, count) -> {
                                    ((Modifiers)vault.get(Vault.MODIFIERS)).addModifier(VaultModifierRegistry.get(modifierx), count, true, random);
                                });
                                Iterator var24 = ((Listeners)vault.get(Vault.LISTENERS)).getAll().iterator();

                                while(var24.hasNext()) {
                                    Listener listener = (Listener)var24.next();
                                    listener.getPlayer().ifPresent((other) -> {
                                        world.playSound((Player)null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                                        other.displayClientMessage(text, false);
                                    });
                                }
                            }
                        }

                        data.setResult(InteractionResult.SUCCESS);
                    }
                }
            }
        });
        CommonEvents.MONOLITH_UPDATE.register(this, (data) -> {
            if (data.getWorld() == world) {
                if (!data.getEntity().isGenerated() || data.getEntity().isOverStacking() != (Integer)this.get(COUNT) >= (Integer)this.get(TARGET) && data.getState().getValue(MonolithBlock.STATE) == MonolithBlock.State.EXTINGUISHED) {
                    data.getEntity().setOverStacking((Integer)this.get(COUNT) >= (Integer)this.get(TARGET));
                    if (data.getEntity().isOverStacking()) {
                        data.getEntity().removeModifiers();
                    }

                    ResourceLocation pool = data.getEntity().isOverStacking() ? (ResourceLocation)this.get(OVER_STACK_MODIFIER_POOL) : (ResourceLocation)this.get(STACK_MODIFIER_POOL);
                    if (pool != null) {
                        int level = (Integer)((VaultLevel)vault.get(Vault.LEVEL)).getOr(VaultLevel.VALUE, 0);
                        ChunkRandom random = ChunkRandom.any();
                        random.setBlockSeed((Long)vault.get(Vault.SEED), data.getPos(), 90039737L);
                        Iterator var7 = ModConfigs.VAULT_MODIFIER_POOLS.getRandom(pool, level, random).iterator();

                        while(var7.hasNext()) {
                            VaultModifier<?> modifier = (VaultModifier)var7.next();
                            data.getEntity().addModifier(modifier);
                        }
                    }

                    data.getEntity().setGenerated(true);
                }
            }
        });
        ((ObjList)this.get(CHILDREN)).forEach((child) -> {
            child.initServer(world, vault);
        });
    }

    @Override
    public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
        if ((Integer)this.get(COUNT) < (Integer)this.get(TARGET)) {
            return objective == this;
        } else {
            Iterator var3 = ((ObjList)this.get(CHILDREN)).iterator();

            Objective child;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                child = (Objective)var3.next();
            } while(!child.isActive(world, vault, objective));

            return true;
        }
    }

    @Override
    public void tickServer(VirtualWorld world, Vault vault) {
        double increase = CommonEvents.OBJECTIVE_TARGET.invoke(world, vault, 0.0).getIncrease();
        this.set(TARGET, (int)Math.round((double)(Integer)this.get(BASE_TARGET) * (1.0 + increase)));
        if ((Integer)this.get(COUNT) >= (Integer)this.get(TARGET)) {
            ((ObjList)this.get(CHILDREN)).forEach((child) -> {
                child.tickServer(world, vault);
            });
        }

    }

    @Override
    protected void playActivationEffects(VirtualWorld world, BlockPos pos, boolean overStacking) {
        if (overStacking) {
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(pos));
            Vec3 vec3 = new Vec3((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F));
            world.sendParticles(particle, vec3.x, vec3.y, vec3.z, 400, 1.0, 1.0, 1.0, 0.5);
            world.sendParticles(ParticleTypes.SCRAPE, vec3.x, vec3.y, vec3.z, 50, 1.0, 1.0, 1.0, 0.5);
            world.playSound((Player)null, vec3.x, vec3.y, vec3.z, ModSounds.DESTROY_MONOLITH, SoundSource.PLAYERS, 0.25F, 1.0F);
        } else {
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new MonolithIgniteMessage(pos));
            world.playSound((Player)null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
        int current;
        FormattedCharSequence var10001;
        float var10002;
        if ((Integer)this.get(COUNT) >= (Integer)this.get(TARGET)) {
            current = window.getGuiScaledWidth() / 2;
            Font font = Minecraft.getInstance().font;
            MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Component txt = (new TextComponent("Grave Rob, or Exit to Complete")).withStyle(ChatFormatting.AQUA);
            var10001 = txt.getVisualOrderText();
            var10002 = (float)current - (float)font.width(txt) / 2.0F;
            Objects.requireNonNull(font);
            font.drawInBatch(var10001, var10002, 9.0F, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
            buffer.endBatch();
            return true;
        } else {
            current = (Integer)this.get(COUNT);
            int total = (Integer)this.get(TARGET);
            Component txt = (new TextComponent(String.valueOf(current))).withStyle(ChatFormatting.WHITE).append((new TextComponent(" / ")).withStyle(ChatFormatting.WHITE)).append((new TextComponent(String.valueOf(total))).withStyle(ChatFormatting.WHITE));
            int midX = 0;
            matrixStack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int previousTexture = RenderSystem.getShaderTexture(0);
            RenderSystem.setShaderTexture(0, HAUNTED_HUD);
            float progress = (float)current / (float)total;
            matrixStack.translate((double)(midX - 80), 8.0, 0.0);
            GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 100);
            GuiComponent.blit(matrixStack, 0, 8, 0.0F, 30.0F, 13 + (int)(130.0F * progress), 10, 200, 100);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, previousTexture);
            matrixStack.popPose();
            Font font = Minecraft.getInstance().font;
            MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            matrixStack.pushPose();
            matrixStack.scale(0.6F, 0.6F, 0.6F);
            var10001 = txt.getVisualOrderText();
            var10002 = (float)midX / 0.6F - (float)font.width(txt) / 2.0F;
            Objects.requireNonNull(font);
            font.drawInBatch(var10001, var10002, (float)(9 + 22), -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
            buffer.endBatch();
            matrixStack.popPose();
            return true;
        }
    }

    static {
        //H_KEY = SupplierKey.of("haunted_braziers", Objective.class).with(Version.v1_2, HauntedBraziersObjective::new);
    }
}
