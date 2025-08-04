package mekanism.api.fabric.lookup;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.function.BooleanSupplier;

public interface BlockApiCacheWithContext<A, C> extends BlockApiCache<A, C> {
    A find();

    C getContext();

    /**
     * Create a new instance bound to the passed {@link ServerLevel} and position, and querying the same API as the passed lookup.
     */
    static <A, C> BlockApiCacheWithContext<A, C> create(BlockApiLookup<A, C> lookup, ServerLevel world, BlockPos pos, C context) {
        return create(lookup, world, pos, context, () -> true, () -> {});
    }

    /**
     * Create a new instance bound to the passed {@link ServerLevel} and position, and querying the same API as the passed lookup.
     */
    static <A, C> BlockApiCacheWithContext<A, C> create(BlockApiLookup<A, C> lookup, ServerLevel world, BlockPos pos, C context, BooleanSupplier isValid, Runnable invalidationListener) {
        return new BlockApiCacheWithContextImpl<>(BlockApiCache.create(lookup, world, pos), context, isValid, invalidationListener);
    }
}
