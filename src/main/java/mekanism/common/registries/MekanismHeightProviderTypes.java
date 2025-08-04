package mekanism.common.registries;

import mekanism.common.Mekanism;
import mekanism.common.world.height.ConfigurableHeightProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;

public class MekanismHeightProviderTypes {

    private MekanismHeightProviderTypes() {
    }

    public static final DeferredRegister<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES = DeferredRegister.create(Registries.HEIGHT_PROVIDER_TYPE, Mekanism.MODID);

    public static final DeferredHolder<HeightProviderType<?>, HeightProviderType<ConfigurableHeightProvider>> CONFIGURABLE = HEIGHT_PROVIDER_TYPES.register("configurable", () -> () -> ConfigurableHeightProvider.CODEC);
}