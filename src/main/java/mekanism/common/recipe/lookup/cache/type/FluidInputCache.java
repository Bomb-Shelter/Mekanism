package mekanism.common.recipe.lookup.cache.type;

import io.github.fabricators_of_create.porting_lib.resources.fluids.FluidStackLinkedSet;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.CompoundFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.DataComponentFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.FluidIngredient;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.world.level.material.Fluid;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public class FluidInputCache<RECIPE extends MekanismRecipe<?>> extends ComponentSensitiveInputCache<Fluid, FluidStack, FluidStackIngredient, RECIPE> {

    public FluidInputCache() {
        super(FluidStackLinkedSet.TYPE_AND_COMPONENTS);
    }

    @Override
    public boolean mapInputs(RECIPE recipe, FluidStackIngredient inputIngredient) {
        return mapIngredient(recipe, inputIngredient.ingredient().ingredient());
    }

    private boolean mapIngredient(RECIPE recipe, FluidIngredient input) {
        if (input.isSimple()) {
            //Simple ingredients don't actually check anything related to NBT,
            // so we can add the items to our base/raw input cache directly
            for (FluidStack fluid : input.getStacks()) {
                if (!fluid.isEmpty()) {
                    //Ignore empty stacks as some mods have ingredients that some stacks are empty
                    addInputCache(fluid.getFluid(), recipe);
                }
            }
        } else if (input instanceof CompoundFluidIngredient compoundIngredient) {
            //Special handling for neo's compound ingredient to map all children as best as we can
            // as maybe some of them are simple
            boolean result = false;
            for (FluidIngredient child : compoundIngredient.children()) {
                result |= mapIngredient(recipe, child);
            }
            return result;
        } else if (input instanceof DataComponentFluidIngredient componentIngredient && componentIngredient.isStrict()) {
            //Special handling for neo's NBT Ingredient as it requires an exact component match
            for (FluidStack fluid : input.getStacks()) {
                //Note: We copy it with a count of one, as we need to copy it anyway to ensure nothing somehow causes our backing map to mutate it,
                // so while we are at it, we just set the size to one, as we don't care about the size
                addNbtInputCache(fluid.copyWithAmount(1), recipe);
            }
        } else {
            //Else it is a custom ingredient, so we don't have a great way of handling it using the normal extraction checks
            // and instead have to just mark it as complex and test as needed
            return true;
        }
        return false;
    }

    @Override
    protected Fluid createKey(FluidStack stack) {
        return stack.getFluid();
    }

    @Override
    public boolean isEmpty(FluidStack input) {
        return input.isEmpty();
    }
}