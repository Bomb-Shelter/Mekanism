package mekanism.api.fabric.lookup;

import mekanism.api.MekanismAPI;
import mekanism.api.fabric.transfer.energy.IEnergyStorage;
import mekanism.api.fabric.transfer.fluids.IFluidHandler;
import mekanism.api.fabric.transfer.fluids.IFluidHandlerItem;
import mekanism.api.fabric.transfer.items.IItemHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Capabilities {
    public static final class EnergyStorage {
        public static final BlockApiLookup<IEnergyStorage, @Nullable Direction> BLOCK = BlockApiLookup.get(create("energy"), IEnergyStorage.class, Direction.class);
        public static final EntityApiLookup<IEnergyStorage, @Nullable Direction> ENTITY = EntityApiLookup.get(create("energy"), IEnergyStorage.class, Direction.class);
        public static final ItemApiLookup<IEnergyStorage, @Nullable Void> ITEM = ItemApiLookup.get(create("energy"), IEnergyStorage.class, void.class);

        private EnergyStorage() {}
    }

    public static final class FluidHandler {
        public static final BlockApiLookup<IFluidHandler, @Nullable Direction> BLOCK = BlockApiLookup.get(create("fluid_handler"), IFluidHandler.class, Direction.class);
        public static final EntityApiLookup<IFluidHandler, @Nullable Direction> ENTITY = EntityApiLookup.get(create("fluid_handler"), IFluidHandler.class, Direction.class);
        public static final ItemApiLookup<IFluidHandlerItem, @Nullable Void> ITEM = ItemApiLookup.get(create("fluid_handler"), IFluidHandlerItem.class, void.class);

        private FluidHandler() {}
    }

    public static final class ItemHandler {
        public static final BlockApiLookup<IItemHandler, @Nullable Direction> BLOCK = BlockApiLookup.get(create("item_handler"), IItemHandler.class, Direction.class);
        public static final EntityApiLookup<IItemHandler, @Nullable Void> ENTITY = EntityApiLookup.get(create("item_handler"), IItemHandler.class, void.class);
        public static final ItemApiLookup<IItemHandler, @Nullable Void> ITEM = ItemApiLookup.get(create("item_handler"), IItemHandler.class, void.class);

        private ItemHandler() {}
    }

    private static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, path);
    }
}
