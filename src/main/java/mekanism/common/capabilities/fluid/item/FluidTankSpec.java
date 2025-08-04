package mekanism.common.capabilities.fluid.item;

import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.attachments.containers.fluid.ComponentBackedFluidTank;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.capabilities.GenericTankSpec;
import net.minecraft.world.item.ItemStack;
import io.github.fabricators_of_create.porting_lib.common.util.TriPredicate;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankSpec extends GenericTankSpec<FluidStack> {

    private final LongSupplier rate;
    private final LongSupplier capacity;

    public FluidTankSpec(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
          TriPredicate<@NotNull FluidStack, @NotNull AutomationType, @NotNull ItemStack> canInsert, Predicate<@NotNull FluidStack> isValid,
          Predicate<@NotNull ItemStack> supportsStack) {
        super(canExtract, canInsert, isValid, supportsStack);
        this.rate = rate;
        this.capacity = capacity;
    }

    public <TANK extends IExtendedFluidTank> TANK createTank(TankFromSpecCreator<TANK> tankCreator, ItemStack stack) {
        return tankCreator.create(rate, capacity, canExtract, (fluid, automationType) -> canInsert.test(fluid, automationType, stack), isValid, null);
    }

    //TODO - 1.20.5: Re-evaluate this
    public void addTank(FluidTanksBuilder builder, ComponentTankFromSpecCreator tankCreator) {
        builder.addTank(((type, attachedTo, containerIndex) -> tankCreator.create(attachedTo, containerIndex, canExtract,
              (chemical, automationType) -> canInsert.test(chemical, automationType, attachedTo), isValid, rate, capacity)));
    }

    public static FluidTankSpec create(LongSupplier rate, LongSupplier capacity) {
        return new FluidTankSpec(rate, capacity, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueTri(), ConstantPredicates.alwaysTrue(),
              ConstantPredicates.alwaysTrue());
    }

    public static FluidTankSpec createFillOnly(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull FluidStack> isValid) {
        return createFillOnly(rate, capacity, isValid, ConstantPredicates.alwaysTrue());
    }

    public static FluidTankSpec createFillOnly(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull FluidStack> isValid,
          Predicate<@NotNull ItemStack> supportsStack) {
        return new FluidTankSpec(rate, capacity, ConstantPredicates.notExternal(), (chemical, automation, stack) -> supportsStack.test(stack), isValid, supportsStack);
    }

    @FunctionalInterface
    public interface ComponentTankFromSpecCreator {

        ComponentBackedFluidTank create(ItemStack attachedTo, int tankIndex, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
              BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> isValid, LongSupplier rate, LongSupplier capacity);
    }

    @FunctionalInterface
    public interface TankFromSpecCreator<TANK extends IExtendedFluidTank> {

        TANK create(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
              BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> isValid, @Nullable IContentsListener listener);

        default TANK create(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
              BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> isValid) {
            return create(rate, capacity, canExtract, canInsert, isValid, null);
        }
    }
}