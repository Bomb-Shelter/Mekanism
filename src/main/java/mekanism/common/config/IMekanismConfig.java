package mekanism.common.config;

import mekanism.common.config.value.CachedValue;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec;

public interface IMekanismConfig {

    String getFileName();

    String getTranslation();

    ModConfigSpec getConfigSpec();

    default boolean isLoaded() {
        return getConfigSpec().isLoaded();
    }

    ModConfig.Type getConfigType();

    void save();

    void clearCache(boolean unloading);

    void addCachedValue(CachedValue<?> configValue);
}