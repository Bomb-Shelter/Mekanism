package mekanism.common.registration.impl;

import mekanism.api.chemical.Chemical;
import mekanism.common.registration.DoubleWrappedRegistryObject;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class SlurryRegistryObject<DIRTY extends Chemical, CLEAN extends Chemical> extends DoubleWrappedRegistryObject<Chemical, DIRTY, Chemical, CLEAN> {

    public SlurryRegistryObject(DeferredChemical<DIRTY> dirtyRO, DeferredChemical<CLEAN> cleanRO) {
        super(dirtyRO, cleanRO);
    }

    @NotNull
    public DeferredHolder<Chemical, CLEAN> getCleanSlurry() {
        return secondaryRO;
    }
}