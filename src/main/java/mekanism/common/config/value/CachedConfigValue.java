package mekanism.common.config.value;

import mekanism.common.config.IMekanismConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec.ConfigValue;

public class CachedConfigValue<T> extends CachedResolvableConfigValue<T, T> {

    protected CachedConfigValue(IMekanismConfig config, ConfigValue<T> internal) {
        super(config, internal);
    }

    public static <T> CachedConfigValue<T> wrap(IMekanismConfig config, ConfigValue<T> internal) {
        return new CachedConfigValue<>(config, internal);
    }

    @Override
    protected T resolve(T encoded) {
        return encoded;
    }

    @Override
    protected T encode(T value) {
        return value;
    }
}