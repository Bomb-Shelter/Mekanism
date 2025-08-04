package mekanism.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedLongValue;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public enum FluidTankTier implements ITier {
    BASIC(BaseTier.BASIC, 32 * FluidConstants.BUCKET, FluidConstants.BUCKET),
    ADVANCED(BaseTier.ADVANCED, 64 * FluidConstants.BUCKET, 4 * FluidConstants.BUCKET),
    ELITE(BaseTier.ELITE, 128 * FluidConstants.BUCKET, 16 * FluidConstants.BUCKET),
    ULTIMATE(BaseTier.ULTIMATE, 256 * FluidConstants.BUCKET, 64 * FluidConstants.BUCKET),
    CREATIVE(BaseTier.CREATIVE, Integer.MAX_VALUE, Integer.MAX_VALUE / 2);

    private final long baseStorage;
    private final long baseOutput;
    private final BaseTier baseTier;
    private CachedLongValue storageReference;
    private CachedLongValue outputReference;

    FluidTankTier(BaseTier tier, long s, long o) {
        baseStorage = s;
        baseOutput = o;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getOrDefault();
    }

    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getOrDefault();
    }

    public long getBaseStorage() {
        return baseStorage;
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the FluidTankTier a reference to the actual config value object
     */
    public void setConfigReference(CachedLongValue storageReference, CachedLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}