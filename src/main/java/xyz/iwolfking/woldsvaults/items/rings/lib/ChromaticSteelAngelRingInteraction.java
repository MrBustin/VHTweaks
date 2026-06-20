package xyz.iwolfking.woldsvaults.items.rings.lib;

import dev.denismasterherobrine.angelring.compat.curios.AbstractRingCurio;
import dev.denismasterherobrine.angelring.config.Configuration;
import dev.denismasterherobrine.angelring.item.utils.EnergyItem;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;
import xyz.iwolfking.woldsvaults.init.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChromaticSteelAngelRingInteraction {
    public ChromaticSteelAngelRingInteraction() {
    }

    public static ICapabilityProvider initCapabilities(final ItemStack stack) {
        final ICurio curio = new AbstractRingCurio(ModItems.CHROMATIC_STEEL_ANGEL_RING) {
            private final int fePerTick;

            {
                this.fePerTick = (Integer)Configuration.EnergeticFEPerTick.get();
            }

            public ItemStack getStack() {
                return stack;
            }

            private @NotNull IEnergyStorage getEnergyStorage(ItemStack stackx) {
                return (IEnergyStorage)this.getStack().getCapability(CapabilityEnergy.ENERGY).resolve().get();
            }

            protected boolean checkIfAllowedToFly(Player player, ItemStack stackx) {
                return this.getEnergyStorage(this.getStack()).getEnergyStored() > 1;
            }

            protected TranslatableComponent getNotAbleToFlyMessage() {
                return new TranslatableComponent("item.angelring.energetic_angel_ring.not_enough_fe");
            }

            protected void payForFlight(Player player, ItemStack stackx) {
                this.getEnergyStorage(this.getStack()).extractEnergy(this.fePerTick, false);
            }

        };
        final EnergyItem energyItem = new EnergyItem(stack, (Integer)Configuration.EnergeticFECapacity.get());
        return new ICapabilityProvider() {
            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> {
                return curio;
            });
            private final LazyOptional<IEnergyStorage> energyStorageOpt = LazyOptional.of(() -> {
                return energyItem;
            });

            @Nonnull
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return cap == CapabilityEnergy.ENERGY ? this.energyStorageOpt.cast() : CuriosCapability.ITEM.orEmpty(cap, this.curioOpt);
            }


        };
    }

    public static boolean isRingInCuriosSlot(ItemStack angelRing, LivingEntity player) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(angelRing.getItem(), player).isPresent();
    }
}
