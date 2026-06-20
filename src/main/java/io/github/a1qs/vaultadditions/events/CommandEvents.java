package io.github.a1qs.vaultadditions.events;


import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.commands.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID)
public class CommandEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new ConfigCommands(event.getDispatcher());
        new PowerSkillCommands(event.getDispatcher());
        new CrystalCommands(event.getDispatcher());
        new EventCommands(event.getDispatcher());
        new SpecialCommands(event.getDispatcher());
        new DebugCommands(event.getDispatcher());
        new WitherGlobalSoundCommands(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

}
