package mekanism.client.recipe_viewer.emi;

import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.FabricEmiStack;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.recipe_viewer.GuiElementHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public class EmiStackUnderMouseProvider implements EmiStackProvider<Screen> {

    @Override
    public EmiStackInteraction getStackAt(Screen screen, int x, int y) {
        if (screen instanceof GuiMekanism<?> gui) {
            return GuiElementHandler.getClickableIngredientUnderMouse(gui, x, y, (helper, ingredient) -> {
                EmiStack emiStack;
                switch (ingredient) {
                    case ItemStack stack -> emiStack = EmiStack.of(stack);
                    case FluidStack stack -> emiStack = FabricEmiStack.of(stack.getVariant(), stack.getAmount());
                    case ChemicalStack stack -> emiStack = new ChemicalEmiStack(stack);
                    default -> {
                        return null;
                    }
                }
                return new EmiStackInteraction(emiStack, null, false);
            }).orElse(EmiStackInteraction.EMPTY);
        }
        return EmiStackInteraction.EMPTY;
    }
}