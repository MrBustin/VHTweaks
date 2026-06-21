package xyz.iwolfking.woldsvaults.init.overworld;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OverworldVaultSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WoldsVaults.MOD_ID);

    public static final RegistryObject<SoundEvent> PORTAL_SPAWN = registerSoundEvent("portal_spawn");




    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus){
        SOUNDS.register(eventBus);
    }
}
