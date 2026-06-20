package io.github.a1qs.vaultadditions.events;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SuperSecretCakeEvent {
    public static final String[] CAKE_QUOTES = new String[]{
            "The cake is a lie",
            "You can have cake and eat it too?",
            "Would like some tea with that?",
            "The cake equals Ï€ (Pi) ?",
            "This cake is made with love",
            "DONT GET GREEDY",
            "The cake is a pine?",
            "That'll go right to your thighs",
            "Have you got the coffee?",
            "When life gives you cake, you eat it",
            "The cake says 'goodbye'",
            "The pie want to cry",
            "It's a piece of cake to bake a pretty cake",
            "The cherries are a lie",
            "1000 calories",
            "Icing on the cake!",
            "Happy Birthday! Is it your birthday?",
            "This is caketastic!",
            "An actual pie chart",
            "Arrr! I'm a Pie-rate",
            "Not every pies in the world is round, sometimes... pi * r ^ 2",
            "Tell me lies, tell me sweet little pies",
            "Diet...? What diet!!!!",
            "I'll take the three story pie and a diet coke... don't want to get fat",
            "This is the end of all cake"
    };

    @SubscribeEvent
    public static void onCakePlaced(BlockEvent.EntityPlaceEvent event) {
        if (!event.getWorld().isClientSide()) {
            // Check the dimension's namespace
            if (event.getWorld() instanceof ServerLevel serverLevel) {
                ResourceKey<Level> dimensionKey = serverLevel.dimension();
                if ("the_vault".equals(dimensionKey.location().getNamespace())) {
                    if (event.getPlacedBlock().getBlock() == Blocks.CAKE) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCakeEat(PlayerInteractEvent.RightClickBlock event) {
        if ("the_vault".equals(event.getWorld().dimension().location().getNamespace())) {
            if(!event.getWorld().getBlockState(event.getHitVec().getBlockPos()).is(Blocks.CAKE)) return;
            if(event.getPlayer().isSpectator()) return;

            if(event.getWorld().isClientSide()) {
                Random random = new Random();
                String cakeQuote = CAKE_QUOTES[random.nextInt(CAKE_QUOTES.length)];
                TextComponent text = new TextComponent("\"" + cakeQuote + "\"");

                text.setStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromRgb(-15343)));
                event.getPlayer().displayClientMessage(text, true);
            } else if(event.getWorld() instanceof ServerLevel level && event.getPlayer() instanceof ServerPlayer player) {
                level.destroyBlock(event.getPos(), false);
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0));
                event.setCanceled(true);
            }
        }
    }
}
