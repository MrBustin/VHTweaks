//
// Created by BONNe
// Copyright - 2024
//


package xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.time.modifier.ModifierExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.Configs;
import xyz.iwolfking.woldsvaults.configs.vault.entry.FruitEntry;
import xyz.iwolfking.woldsvaults.configs.vault.entry.ModifierCounter;
import xyz.iwolfking.woldsvaults.data.CakeWorldData;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Mixin(value = CakeObjective.class, remap = false)
public abstract class MixinCakeObjective {
    @Unique
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#.##");

    @Inject(method = "initServer", at = @At("TAIL"))
    private void injectModifiers(VirtualWorld world, Vault vault, CallbackInfo ci) {
        if (vault.getOptional(Vault.LISTENERS).flatMap(listeners -> listeners.getOptional(Listeners.LOGIC)).
                map(logic -> logic.has(ClassicListenersLogic.ADDED_BONUS_TIME)).orElse(false)) {
            return;
        }

        RandomSource random = ChunkRandom.ofInternal(vault.get(Vault.SEED));
        if (!(Configs.FRUIT_CAKE.getChance() > random.nextFloat())) {
            return;
        }

        WoldsVaults.LOGGER.debug("Fruit Cake Vault Triggered.");
        CakeWorldData cakeData = CakeWorldData.get(world);
        cakeData.setFruitCake(true);
        cakeData.setCakeType("");
        vault.ifPresent(Vault.MODIFIERS, modifiers -> {
            for (ModifierCounter startModifier : Configs.FRUIT_CAKE.getModifiers()) {
                if (startModifier.modifier() != null) {
                    modifiers.addModifier(startModifier.modifier(), startModifier.count(), true, random);
                    WoldsVaults.LOGGER.debug("Add modifier: {} x {} to fruit cake vault.", startModifier.modifier().getDisplayName(), startModifier.count());
                }
            }
        });
    }

    @ModifyArg(method = "addModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V"))
    private String fruitCakeModifierDisplay(String value, @Local(argsOnly = true) VirtualWorld world) {
        if (value.equals("cake")) {
            CakeWorldData cakeData = CakeWorldData.get(world);
            FruitEntry fruit = Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType());
            return cakeData.isFruitCake() && fruit != null ? Configs.FRUIT_CAKE.getFruitCakeFormat().replace("%fruit%", fruit.displayName) : value;
        }
        return value;
    }

    @ModifyVariable(method = "addModifier", name = "suffix", at = @At(value = "STORE", ordinal = 0))
    private TextComponent replaceSuffix(TextComponent suffix, @Local(argsOnly = true) VirtualWorld world) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (cakeData.isFruitCake() && Configs.FRUIT_CAKE.isReplaceSliceWithTimeAdded()) {
            FruitEntry fruit = Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType());
            return fruit != null ? new TextComponent(Configs.FRUIT_CAKE.getTimeAddedFormat().replace("%seconds%", TIME_FORMAT.format(fruit.ticks / 20.0))) : suffix;
        }
        return suffix;
    }

    @Redirect(method = "addModifier", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectIterator;hasNext()Z"))
    private boolean cancelNormalSuffix(ObjectIterator<?> instance, @Local(argsOnly = true) VirtualWorld world) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (cakeData.isFruitCake() && Configs.FRUIT_CAKE.isReplaceSliceWithTimeAdded() && Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType()) != null) {
            return false;
        }
        return instance.hasNext();
    }

    @Inject(method = "addModifier", at = @At("TAIL"))
    private void injectTimeAdder(VirtualWorld world, Vault vault, Player player, RandomSource random, CallbackInfo ci) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (!cakeData.isFruitCake()) {
            return;
        }

        FruitEntry fruit = Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType());
        if (fruit != null) {
            vault.ifPresent(Vault.CLOCK, clock -> clock.addModifier(new ModifierExtension(fruit.ticks)));
        }
    }

    @Inject(method = "generateCake", at = @At(value = "TAIL"))
    private void injectItemSpawning(VirtualWorld world, RegionPos region, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (!cakeData.isFruitCake()) {
            WoldsVaults.LOGGER.debug("Not a fruit cake!");
            return;
        }

        int memberCount = ServerVaults.get(world).flatMap(vault -> vault.getOptional(Vault.STATS)).map(stats -> stats.getMap().size()).orElse(0);
        Map.Entry<Float, FruitEntry> floatFruitEntry = Configs.FRUIT_CAKE.getFruitChances(memberCount).higherEntry(random.nextFloat());

        if (floatFruitEntry != null) {
            FruitEntry fruit = floatFruitEntry.getValue();
            cakeData.setCakeType(fruit.id);
            this.moreObjectives$spawnItemParticles(world, ((CakeObjective) (Object) this).get(CakeObjective.CAKE_POS), new ItemStack(fruit.iconItem, 1));
            WoldsVaults.LOGGER.debug("Next cake {}", cakeData.getLastCakeType());
        } else {
            cakeData.setCakeType("");
            WoldsVaults.LOGGER.debug("Failed to get next cake. Check your fruit list.");
        }
    }

    @Inject(method = "doEatingEffects", at = @At("TAIL"))
    private void injectItemRemoving(VirtualWorld world, BlockPos pos, CallbackInfo ci) {
        List<ItemEntity> itemStacks = world.getEntitiesOfClass(ItemEntity.class,
                AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 1, 2, 1),
                item -> item.getTags().contains("PreventMagnetMovement") && item.isNoGravity() && item.isInvulnerable());

        if (itemStacks.size() == 1) {
            itemStacks.get(0).discard();
        }
    }

    @Unique
    public void moreObjectives$spawnItemParticles(ServerLevel world, BlockPos pos, ItemStack itemStack) {
        ItemEntity displayEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, itemStack);
        displayEntity.setNeverPickUp();
        displayEntity.setUnlimitedLifetime();
        displayEntity.setNoGravity(true);
        displayEntity.setInvulnerable(true);
        displayEntity.addTag("PreventMagnetMovement");
        displayEntity.getPersistentData().putBoolean("PreventRemoteMovement", true);
        displayEntity.setDeltaMovement(0, 0, 0);
        world.addFreshEntity(displayEntity);
    }
}
