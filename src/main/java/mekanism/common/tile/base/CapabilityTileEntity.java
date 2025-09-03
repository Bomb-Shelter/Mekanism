package mekanism.common.tile.base;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.fabric.lookup.ICapabilityProvider;
import mekanism.api.fabric.transfer.fluids.IFluidHandler;
import mekanism.api.fabric.transfer.items.IItemHandler;
import mekanism.api.heat.IHeatHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.CapabilityCache;
import mekanism.common.capabilities.resolver.ICapabilityResolver;
import mekanism.common.capabilities.resolver.manager.ICapabilityHandlerManager;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.component.TileComponentConfig;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CapabilityTileEntity extends TileEntityUpdateable {

    //Note: The below providers assume that the capability if supported has been added by either addCapabilityResolver or addCapabilityResolvers
    public static final ICapabilityProvider<CapabilityTileEntity, @Nullable Direction, IChemicalHandler> CHEMICAL_HANDLER_PROVIDER = basicCapabilityProvider(Capabilities.CHEMICAL.block());
    public static final ICapabilityProvider<CapabilityTileEntity, @Nullable Direction, IHeatHandler> HEAT_HANDLER_PROVIDER = basicCapabilityProvider(Capabilities.HEAT);
    public static final ICapabilityProvider<CapabilityTileEntity, @Nullable Direction, IItemHandler> ITEM_HANDLER_PROVIDER = basicCapabilityProvider(Capabilities.ITEM.block());
    public static final ICapabilityProvider<CapabilityTileEntity, @Nullable Direction, IFluidHandler> FLUID_HANDLER_PROVIDER = basicCapabilityProvider(Capabilities.FLUID.block());

    public static <CAP> ICapabilityProvider<CapabilityTileEntity, @Nullable Direction, CAP> basicCapabilityProvider(BlockApiLookup<CAP, @Nullable Direction> capability) {
        return (tile, context) -> {
            if (tile.capabilityCache.isCapabilityDisabled(capability, context)) {
                return null;
            }
            ICapabilityResolver<@Nullable Direction> resolver = tile.capabilityCache.getResolver(capability);
            return resolver == null ? null : resolver.resolve(capability, context);
        };
    }

    public static <TILE extends CapabilityTileEntity, CAP> ICapabilityProvider<TILE, @Nullable Direction, CAP> capabilityProvider(
          BlockApiLookup<CAP, @Nullable Direction> capability, BiFunction<TILE, BlockApiLookup<CAP, @Nullable Direction>, ICapabilityResolver<@Nullable Direction>> resolverGetter) {
        return (tile, context) -> {
            CapabilityCache capabilityCache = ((CapabilityTileEntity) tile).capabilityCache;
            if (capabilityCache.isCapabilityDisabled(capability, context)) {
                return null;
            }
            return capabilityCache.getResolver(capability, () -> resolverGetter.apply(tile, capability))
                  .resolve(capability, context);
        };
    }

    private final CapabilityCache capabilityCache = new CapabilityCache();

    public CapabilityTileEntity(TileEntityTypeRegistryObject<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected final void addCapabilityResolvers(List<ICapabilityHandlerManager<?>> capabilityHandlerManagers) {
        for (ICapabilityHandlerManager<?> capabilityHandlerManager : capabilityHandlerManagers) {
            //Add all managers that we support in our tile, as capability resolvers
            if (capabilityHandlerManager.canHandle()) {
                capabilityCache.addCapabilityResolver(capabilityHandlerManager);
            }
        }
    }

    protected final void addCapabilityResolver(ICapabilityResolver<@Nullable Direction> resolver) {
        capabilityCache.addCapabilityResolver(resolver);
    }

    protected final void addConfigComponent(TileComponentConfig config) {
        capabilityCache.addConfigComponent(config);
    }

    /**
     * Invalidates our backing internal representations for certain capabilities in addition to actually notifying the level of capability invalidation.
     */
    public void invalidateCapabilitiesFull() {
        //Clear our internal cached capability instances and then invalidate the capabilities to the world
        // that way when queried from the invalidation listener we will ensure we can provide the up to date instance
        capabilityCache.invalidateAll();
        invalidateCapabilities(Collections.emptyList(), null);
    }

    @Override
    public void setRemoved() {
        //Note: Clear the backing caps before letting super invalidate as then if anything somehow queries us in their invalidation listeners
        // they will get the proper non cached data
        capabilityCache.invalidateAll();
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        //Note: Clear the backing caps before letting super invalidate as then if anything somehow queries us in their invalidation listeners
        // they will get the proper non cached data
        capabilityCache.invalidateAll();
        super.clearRemoved();
    }

    public final void invalidateCapability(@NotNull BlockApiLookup<?, @Nullable Direction> capability, @Nullable Direction side) {
        capabilityCache.invalidate(capability, side);
        invalidateCapabilities(Collections.singleton(capability), side);
    }

    public final void invalidateCapabilityAll(@NotNull BlockApiLookup<?, @Nullable Direction> capability) {
        capabilityCache.invalidateAll(capability);
        invalidateCapabilities(Collections.singleton(capability), null);
    }

    public final void invalidateCapabilities(@NotNull Collection<BlockApiLookup<?, @Nullable Direction>> capabilities, @Nullable Direction side) {
        for (BlockApiLookup<?, @Nullable Direction> capability : capabilities) {
            capabilityCache.invalidate(capability, side);
        }
        invalidateCapabilities(capabilities, side);
    }

    public final void invalidateCapabilitiesAll(@NotNull Collection<BlockApiLookup<?, @Nullable Direction>> capabilities) {
        for (BlockApiLookup<?, @Nullable Direction> capability : capabilities) {
            capabilityCache.invalidateAll(capability);
        }
        invalidateCapabilities(capabilities, null);
    }
}