package mekanism.common.lib.transmitter.acceptor;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fabric.lookup.BlockApiCacheWithContext;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache.CacheBasedInfo;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class AcceptorCache<ACCEPTOR> extends AbstractAcceptorCache<ACCEPTOR, CacheBasedInfo<ACCEPTOR>> {

    private final BlockApiLookup<ACCEPTOR, @Nullable Direction> capability;

    public AcceptorCache(TileEntityTransmitter transmitterTile, BlockApiLookup<ACCEPTOR, @Nullable Direction> capability) {
        super(transmitterTile);
        this.capability = capability;
    }

    @Override
    protected CacheBasedInfo<ACCEPTOR> initializeCache(ServerLevel level, BlockPos pos, Direction opposite, RefreshListener refreshListener) {
        return new CacheBasedInfo<>(BlockApiCacheWithContext.create(capability, level, pos, opposite, refreshListener, refreshListener));
    }

    public record CacheBasedInfo<ACCEPTOR>(BlockApiCacheWithContext<ACCEPTOR, @Nullable Direction> cache) implements AcceptorInfo<ACCEPTOR> {

        @Nullable
        @Override
        public ACCEPTOR acceptor() {
            return cache.find();
        }
    }
}