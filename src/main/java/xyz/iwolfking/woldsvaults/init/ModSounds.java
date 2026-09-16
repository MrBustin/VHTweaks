package xyz.iwolfking.woldsvaults.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static SoundEvent WOLD_AMBIENT;
    public static SoundEvent WOLD_HURT;
    public static SoundEvent WOLD_DEATH;
    public static SoundEvent SAFERSPACES_PROC;
    public static SoundEvent OVERVAULT_PORTAL_SPAWN;
    public static SoundEvent ONE_UP;
    public static SoundEvent SUPER_MUSHROOM;
    public static SoundEvent RANG_RICOCHET;
    public static SoundEvent LOGINAR_IDLE;
    public static SoundEvent LOGINAR_DEATH;
    public static SoundEvent LOGINAR_HURT;
    public static SoundEvent LOGINAR_ATTACK;
    public static SoundEvent WITCHSKALL_IDLE;


    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        WOLD_DEATH = registerSound(event, "wold_death");
        WOLD_HURT = registerSound(event, "wold_hurt");
        WOLD_AMBIENT = registerSound(event, "wold_ambient");
        SAFERSPACES_PROC = registerSound(event, "saferspaces_proc");
        OVERVAULT_PORTAL_SPAWN = registerSound(event, "overworld_portal_spawn");
        ONE_UP = registerSound(event, "one_up");
        SUPER_MUSHROOM = registerSound(event, "super_mushroom");
        RANG_RICOCHET = registerSound(event, "rang_ricochet");
        LOGINAR_IDLE = registerSound(event, "loginar_idle");
        LOGINAR_DEATH = registerSound(event, "loginar_death");
        LOGINAR_HURT = registerSound(event, "loginar_hurt");
        LOGINAR_ATTACK = registerSound(event, "loginar_attack");
        WITCHSKALL_IDLE = registerSound(event, "witchskall_idle");

    }

    private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String soundName) {
        ResourceLocation location = WoldsVaults.id(soundName);
        SoundEvent soundEvent = new SoundEvent(location);
        soundEvent.setRegistryName(location);
        event.getRegistry().register(soundEvent);
        return soundEvent;
    }

    // Merged from the former init.additions class.
public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WoldsVaults.MOD_ID);

    public static final RegistryObject<SoundEvent> BLADE_FRENZY = SOUNDS.register("blade_frenzy",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "blade_frenzy")));

    /* Player Specific sounds */
    public static final RegistryObject<SoundEvent> HOY_ACTIVATE_ARCHON = SOUNDS.register("hoy_activate_archon",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_activate_archon")));

    public static final RegistryObject<SoundEvent> HOY_ARCHON_BOLT = SOUNDS.register("hoy_archon_bolt",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_archon_bolt")));

    public static final RegistryObject<SoundEvent> HOY_DASH = SOUNDS.register("hoy_dash",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_dash")));

    public static final RegistryObject<SoundEvent> HOY_ACTIVATE_MANASHIELD = SOUNDS.register("hoy_activate_manashield",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_activate_manashield")));

    public static final RegistryObject<SoundEvent> HOY_MANASHIELD_HIT = SOUNDS.register("hoy_manashield_hit",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_manashield_hit")));

    public static final RegistryObject<SoundEvent> TIGER_ACTIVATE_ARCHON = SOUNDS.register("tiger_activate_archon",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "tiger_activate_archon")));

    public static final RegistryObject<SoundEvent> TIGER_ARCHON_BOLT = SOUNDS.register("tiger_archon_bolt",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "tiger_archon_bolt")));

    public static final RegistryObject<SoundEvent> TIGER_DASH = SOUNDS.register("tiger_dash",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "tiger_dash")));

    public static final RegistryObject<SoundEvent> TIGER_ACTIVATE_MANASHIELD = SOUNDS.register("tiger_activate_manashield",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "tiger_activate_manashield")));

    public static final RegistryObject<SoundEvent> HOY_ELYTRA_GLIDE = SOUNDS.register("hoy_elytra_glide",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "hoy_elytra_glide")));

    public static final RegistryObject<SoundEvent> FART = SOUNDS.register("fart",
            () -> new SoundEvent(new ResourceLocation(WoldsVaults.MOD_ID, "fart")));
}
