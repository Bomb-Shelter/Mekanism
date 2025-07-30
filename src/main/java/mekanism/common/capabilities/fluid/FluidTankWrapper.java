package mekanism.common.capabilities.fluid;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.merged.ChemicalTankWrapper;
import mekanism.common.capabilities.merged.MergedTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Like {@link ChemicalTankWrapper}
 */
@NothingNullByDefault
public class FluidTankWrapper implements IExtendedFluidTank {

    private final IChemicalTank chemicalTank;
    private final IExtendedFluidTank internal;
    private final MergedTank mergedTank;

    public FluidTankWrapper(MergedTank mergedTank, IExtendedFluidTank internal, IChemicalTank chemicalTank) {
        //TODO: Do we want to short circuit it so that if we are not empty it allows for inserting before checking the insertCheck
        this.mergedTank = mergedTank;
        this.internal = internal;
        this.chemicalTank = chemicalTank;
    }

    public MergedTank getMergedTank() {
        return mergedTank;
    }

    @Override
    public void setStack(FluidStack stack) {
        internal.setStack(stack);
    }

    @Override
    public void setStackUnchecked(FluidStack stack) {
        internal.setStackUnchecked(stack);
    }

    private boolean canInsert() {
        return chemicalTank.isEmpty();
    }

    @Override
    public FluidStack insert(FluidStack stack, Action action, AutomationType automationType) {
        //Only allow inserting if we pass the check
        return canInsert() ? internal.insert(stack, action, automationType) : stack;
    }

    @Override
    public FluidStack extract(long amount, Action action, AutomationType automationType) {
        return internal.extract(amount, action, automationType);
    }

    @Override
    public void onContentsChanged() {
        internal.onContentsChanged();
    }

    @Override
    public long setStackSize(long amount, Action action) {
        return internal.setStackSize(amount, action);
    }

    @Override
    public long growStack(long amount, Action action) {
        return internal.growStack(amount, action);
    }

    @Override
    public long shrinkStack(long amount, Action action) {
        return internal.shrinkStack(amount, action);
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public void setEmpty() {
        internal.setEmpty();
    }

    @Override
    public boolean isFluidEqual(FluidStack other) {
        return internal.isFluidEqual(other);
    }

    @Override
    public long getNeeded() {
        return internal.getNeeded();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return internal.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        internal.deserializeNBT(provider, nbt);
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return internal.getFluid();
    }

    @Override
    public long getFluidAmount() {
        return internal.getFluidAmount();
    }

    @Override
    public long getCapacity() {
        return internal.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return internal.isFluidValid(stack);
    }
}