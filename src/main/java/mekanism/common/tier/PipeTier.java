package mekanism.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedLongValue;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public enum PipeTier implements ITier {
    BASIC(BaseTier.BASIC, 2 * FluidConstants.BUCKET, FluidConstants.BUCKET / 4),
    ADVANCED(BaseTier.ADVANCED, 8 * FluidConstants.BUCKET, FluidConstants.BUCKET),
    ELITE(BaseTier.ELITE, 32 * FluidConstants.BUCKET, 8 * FluidConstants.BUCKET),
    ULTIMATE(BaseTier.ULTIMATE, 128 * FluidConstants.BUCKET, 32 * FluidConstants.BUCKET);

    private final long baseCapacity;
    private final long basePull;
    private final BaseTier baseTier;
    private CachedLongValue capacityReference;
    private CachedLongValue pullReference;

    PipeTier(BaseTier tier, long capacity, long pullAmount) {
        baseCapacity = capacity;
        basePull = pullAmount;
        baseTier = tier;
    }

    public static PipeTier get(BaseTier tier) {
        for (PipeTier transmitter : EnumUtils.PIPE_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return BASIC;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getPipeCapacity() {
        return capacityReference == null ? getBaseCapacity() : capacityReference.getOrDefault();
    }

    public long getPipePullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getOrDefault();
    }

    public long getBaseCapacity() {
        return baseCapacity;
    }

    public long getBasePull() {
        return basePull;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the PipeTier a reference to the actual config value object
     */
    public void setConfigReference(CachedLongValue capacityReference, CachedLongValue pullReference) {
        this.capacityReference = capacityReference;
        this.pullReference = pullReference;
    }
}