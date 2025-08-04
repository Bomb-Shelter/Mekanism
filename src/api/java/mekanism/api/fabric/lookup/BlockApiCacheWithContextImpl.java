package mekanism.api.fabric.lookup;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class BlockApiCacheWithContextImpl<A, C> implements BlockApiCacheWithContext<A, C> {
    private final BlockApiCache<A, C> cache;
    private final C context;

    /**
     * {@code true} if notifications received by the cache will be forwarded to {@link #listener}.
     * By default and after each invalidation, this is set to {@code false}.
     * Calling {@link #find()} sets it to {@code true}.
     */
    private boolean cacheValid = false;
    @Nullable
    private A cachedCap = null;

    private boolean canQuery = true;
    private final ICapabilityInvalidationListener listener;

    public BlockApiCacheWithContextImpl(BlockApiCache<A, C> cache, C context, BooleanSupplier isValid, Runnable invalidationListener) {
        this.cache = cache;
        this.context = context;

        this.listener = () -> {
            if (!cacheValid) {
                // already invalidated, just check if the cache should be removed
                return isValid.getAsBoolean();
            }

            // disable queries for now
            canQuery = false;
            // mark cached cap as invalid
            cacheValid = false;
            // clear cached cap
            cachedCap = null;

            if (isValid.getAsBoolean()) {
                // notify
                invalidationListener.run();
                // re-enable queries
                canQuery = true;
                return true;
            } else {
                // not valid anymore: keep queries disabled and return false
                return false;
            }
        };
    }

    @Override
    public A find() {
        if (!canQuery)
            throw new IllegalStateException("Do not call getCapability on an invalid cache or from the invalidation listener!");

        if (!cacheValid) {
            if (!cache.getWorld().isLoaded(cache.getPos())) {
                // If the position is not loaded, return no capability for now.
                // The cache will be invalidated when the chunk is loaded.
                cachedCap = null;
            } else {
                cachedCap = find(context);
            }
            cacheValid = true;
        }

        return cachedCap;
    }

    @Override
    public C getContext() {
        return context;
    }

    @Override
    public @Nullable A find(@Nullable BlockState state, C context) {
        return cache.find(state, context);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return cache.getBlockEntity();
    }

    @Override
    public BlockApiLookup<A, C> getLookup() {
        return cache.getLookup();
    }

    @Override
    public ServerLevel getWorld() {
        return cache.getWorld();
    }

    @Override
    public BlockPos getPos() {
        return cache.getPos();
    }
}
