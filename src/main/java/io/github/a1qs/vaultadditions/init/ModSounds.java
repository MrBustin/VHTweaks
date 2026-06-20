package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, VaultAdditions.MOD_ID);

    public static final RegistryObject<SoundEvent> BLADE_FRENZY = SOUNDS.register("blade_frenzy",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "blade_frenzy")));

    /* Player Specific sounds */
    public static final RegistryObject<SoundEvent> HOY_ACTIVATE_ARCHON = SOUNDS.register("hoy_activate_archon",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_activate_archon")));

    public static final RegistryObject<SoundEvent> HOY_ARCHON_BOLT = SOUNDS.register("hoy_archon_bolt",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_archon_bolt")));

    public static final RegistryObject<SoundEvent> HOY_DASH = SOUNDS.register("hoy_dash",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_dash")));

    public static final RegistryObject<SoundEvent> HOY_ACTIVATE_MANASHIELD = SOUNDS.register("hoy_activate_manashield",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_activate_manashield")));

    public static final RegistryObject<SoundEvent> HOY_MANASHIELD_HIT = SOUNDS.register("hoy_manashield_hit",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_manashield_hit")));

    public static final RegistryObject<SoundEvent> TIGER_ACTIVATE_ARCHON = SOUNDS.register("tiger_activate_archon",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "tiger_activate_archon")));

    public static final RegistryObject<SoundEvent> TIGER_ARCHON_BOLT = SOUNDS.register("tiger_archon_bolt",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "tiger_archon_bolt")));

    public static final RegistryObject<SoundEvent> TIGER_DASH = SOUNDS.register("tiger_dash",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "tiger_dash")));

    public static final RegistryObject<SoundEvent> TIGER_ACTIVATE_MANASHIELD = SOUNDS.register("tiger_activate_manashield",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "tiger_activate_manashield")));

    public static final RegistryObject<SoundEvent> HOY_ELYTRA_GLIDE = SOUNDS.register("hoy_elytra_glide",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "hoy_elytra_glide")));

    public static final RegistryObject<SoundEvent> FART = SOUNDS.register("fart",
            () -> new SoundEvent(new ResourceLocation(VaultAdditions.MOD_ID, "fart")));
}
