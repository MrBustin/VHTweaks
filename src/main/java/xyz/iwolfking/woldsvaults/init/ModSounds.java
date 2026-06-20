package xyz.iwolfking.woldsvaults.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.woldsvaults.WoldsVaults;

public class ModSounds {

    public static SoundEvent WOLD_AMBIENT;
    public static SoundEvent WOLD_HURT;
    public static SoundEvent WOLD_DEATH;
    public static SoundEvent SAFERSPACES_PROC;
    public static SoundEvent OVERVAULT_PORTAL_SPAWN;
    public static SoundEvent ONE_UP;
    public static SoundEvent SUPER_MUSHROOM;
    public static SoundEvent RANG_RICOCHET;


    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        WOLD_DEATH = registerSound(event, "wold_death");
        WOLD_HURT = registerSound(event, "wold_hurt");
        WOLD_AMBIENT = registerSound(event, "wold_ambient");
        SAFERSPACES_PROC = registerSound(event, "saferspaces_proc");
        OVERVAULT_PORTAL_SPAWN = registerSound(event, "overworld_portal_spawn");
        ONE_UP = registerSound(event, "one_up");
        SUPER_MUSHROOM = registerSound(event, "super_mushroom");
        RANG_RICOCHET = registerSound(event, "rang_ricochet");

    }

    private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String soundName) {
        ResourceLocation location = WoldsVaults.id(soundName);
        SoundEvent soundEvent = new SoundEvent(location);
        soundEvent.setRegistryName(location);
        event.getRegistry().register(soundEvent);
        return soundEvent;
    }
}
