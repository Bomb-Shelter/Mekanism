package mekanism.common.fabric;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

public class FabricUtil {
    public static int getBurnTime(ItemStack stack) {
        var value = FuelRegistry.INSTANCE.get(stack.getItem());

        if (value == null)
            return 0;

        return value;
    }
}
